/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.config;

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.entry.AbstractBasicEntry;
import de.siphalor.tweed4.config.entry.ConfigEntry;
import de.siphalor.tweed4.data.AnnotatedDataValue;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.DataType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigCategory extends AbstractBasicEntry<ConfigCategory> {

	protected Map<String, ConfigEntry<?>> entries = new LinkedHashMap<>();
	protected Identifier backgroundTexture;

	private Runnable reloadListener;

	/**
	 * Adds a new entry to the category
	 * @param name the key used in the data architecture
	 * @param configEntry the entry to add
	 * @see ConfigFile#register(String, ConfigEntry)
	 */
	public <T extends ConfigEntry<?>> T register(String name, T configEntry) {
		entries.put(name, configEntry);
		if(configEntry.getOwnEnvironment() == ConfigEnvironment.DEFAULT) configEntry.setEnvironment(environment);
		if(configEntry.getScope() == ConfigScope.DEFAULT) configEntry.setScope(scope);
		return configEntry;
	}

	public ConfigEntry<?> get(String name) {
		return entries.get(name);
	}

	@Override
	public void reset(@NotNull ConfigEnvironment environment, @NotNull ConfigScope scope) {
		entryStream(environment, scope).forEach(entry -> entry.getValue().reset(environment, scope));
	}

	@Override
	public String getDescription() {
		return comment;
	}

	/**
	 * Sets the background texture for a possible GUI
	 * @param backgroundTexture an identifier to that texture
	 * @return this category for chain calls
	 */
	public ConfigCategory setBackgroundTexture(Identifier backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
		return this;
	}

	/**
	 * Gets the background texture identifier (<b>may be null!</b>)
	 * @return an identifier for the background texture or <b>null</b>
	 */
	public Identifier getBackgroundTexture() {
		return backgroundTexture;
	}

	@Override
	public ConfigCategory setEnvironment(@NotNull ConfigEnvironment environment) {
		super.setEnvironment(environment);
		for (ConfigEntry<?> configEntry : entries.values()) {
			if (configEntry.getOwnEnvironment() == ConfigEnvironment.DEFAULT) {
				configEntry.setEnvironment(environment);
			}
		}
		return this;
	}

	/**
	 * @inheritDoc
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.0")
	public ConfigEnvironment getEnvironment() {
		if (entries.isEmpty()) return environment;
		ConfigEnvironment environment = this.environment;
		for (ConfigEntry<?> configEntry : entries.values()) {
			ConfigEnvironment itEnvironment = configEntry.getOwnEnvironment();
			if (environment == ConfigEnvironment.DEFAULT) {
				environment = itEnvironment;
				continue;
			}
			while (!environment.contains(itEnvironment) && environment.parent != null) {
				environment = environment.parent;
			}
		}
		return environment;
	}

	@Override
	public ConfigCategory setScope(@NotNull ConfigScope scope) {
		super.setScope(scope);
		for (ConfigEntry<?> configEntry : entries.values()) {
			if (configEntry.getScope() == ConfigScope.DEFAULT) {
				configEntry.setScope(scope);
			}
		}
		return this;
	}

	@Override
	public ConfigScope getScope() {
		// In this context this finds the highest scope (the scope that trigger most of the other scopes).
		if(entries.isEmpty()) return scope;

		ConfigScope highest = scope;
		for (ConfigEntry<?> entry : this.entries.values()) {
			ConfigScope entryScope = entry.getScope();
			if (entryScope != highest && entryScope.triggers(highest)) {
				highest = entryScope;
			}
		}
		return highest;
	}

	@Override
	public boolean matches(ConfigEnvironment environment, ConfigScope scope) {
		for (ConfigEntry<?> entry : entries.values()) {
			if (entry.matches(environment, scope)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <V> void read(@NotNull DataSerializer<V> serializer, @NotNull V value, @NotNull ConfigEnvironment environment, @NotNull ConfigScope scope, @NotNull ConfigOrigin origin) throws ConfigReadException {
		DataObject<V> dataObject = serializer.toObject(value);
		entryStream(environment, scope).filter(entry -> dataObject.has(entry.getKey())).forEach(entry -> {
			V fieldValue = dataObject.get(entry.getKey());
			try {
				entry.getValue().read(serializer, fieldValue, environment, scope, origin);
			} catch (ConfigReadException e) {
				Tweed.LOGGER.error("Error reading " + entry.getKey() + ":");
				e.printStackTrace();
				return;
			} catch (Exception e) {
				Tweed.LOGGER.error("Unexpected exception thrown during deserialization of data:");
				e.printStackTrace();
				return;
			}

			Constraint.Result<?> result = entry.getValue().applyConstraints();
			for (Pair<Constraint.Severity, String> message : result.messages) {
				Tweed.LOGGER.log(Level.getLevel(message.getFirst().name()), "Error in constraint: " + message.getSecond());
			}
		});
		onReload();
	}

	@Override
	public void read(@NotNull PacketByteBuf buf, @NotNull ConfigEnvironment environment, @NotNull ConfigScope scope, @NotNull ConfigOrigin origin) {
		while(buf.readBoolean()) {
			ConfigEntry<?> entry = entries.get(buf.readString(32767));
			if (entry != null) {
				entry.read(buf, environment, scope, origin);
			} else {
				throw new RuntimeException("Attempt to sync unknown entry! Aborting.");
			}
		}
		onReload();
	}

	@Override
	public void write(@NotNull PacketByteBuf buf, @NotNull ConfigEnvironment environment, @NotNull ConfigScope scope, @NotNull ConfigOrigin origin) {
		entryStream(environment, scope).forEach(entry -> {
			buf.writeBoolean(true);
			buf.writeString(entry.getKey());
			entry.getValue().write(buf, environment, scope, origin);
		});
		buf.writeBoolean(false);
	}

	@Override
	public <V> AnnotatedDataValue<Object> write(@NotNull DataSerializer<V> serializer, @Nullable AnnotatedDataValue<V> oldValue, @NotNull ConfigEnvironment environment, @NotNull ConfigScope scope) {
		DataObject<V> object;
		if (oldValue != null && oldValue.getValue() instanceof DataObject) {
			//noinspection unchecked
			object = (DataObject<V>) oldValue.getValue();
		} else {
			object = serializer.newObject();
		}
		entryStream(environment, scope).forEach(entry -> {
			AnnotatedDataValue<?> value = entry.getValue().write(serializer, AnnotatedDataValue.of(object.get(entry.getKey())), environment, scope);
			if (value != null) {
				object.putRaw(entry.getKey(), value.getValue());
			}
		});
		return AnnotatedDataValue.of(object, comment.equals("") ? null : comment);
	}

	public Stream<Map.Entry<String, ConfigEntry<?>>> entryStream() {
		return entries.entrySet().stream();
	}

	public Stream<Map.Entry<String, ConfigEntry<?>>> entryStream(ConfigEnvironment environment, ConfigScope scope) {
		return entryStream().filter(entry -> entry.getValue().matches(environment, scope));
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public ConfigCategory setReloadListener(Runnable reloadListener) {
		this.reloadListener = reloadListener;
		return this;
	}

	public void onReload() {
		if (reloadListener != null)
			reloadListener.run();
	}
}
