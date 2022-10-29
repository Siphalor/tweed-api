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

package de.siphalor.tweed5.config;

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed5.Tweed;
import de.siphalor.tweed5.config.constraints.Constraint;
import de.siphalor.tweed5.config.entry.AbstractBasicEntry;
import de.siphalor.tweed5.config.entry.ConfigEntry;
import de.siphalor.tweed5.data.AnnotatedDataValue;
import de.siphalor.tweed5.data.DataObject;
import de.siphalor.tweed5.data.DataSerializer;
import de.siphalor.tweed5.reload.ReloadContext;
import de.siphalor.tweed5.reload.ReloadEnvironment;
import de.siphalor.tweed5.reload.ReloadScope;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
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
		if(configEntry.getOwnEnvironment() == ReloadEnvironment.UNSPECIFIED) configEntry.setEnvironment(getOwnEnvironment());
		if(configEntry.getScope() == ReloadScope.UNSPECIFIED) configEntry.setScope(getOwnScope());
		return configEntry;
	}

	public ConfigEntry<?> get(String name) {
		return entries.get(name);
	}

	@Override
	public void reset(@NotNull ReloadEnvironment environment, @NotNull ReloadScope scope) {
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
	public ConfigCategory setEnvironment(@NotNull ReloadEnvironment environment) {
		super.setEnvironment(environment);
		for (ConfigEntry<?> configEntry : entries.values()) {
			if (configEntry.getOwnEnvironment() == ReloadEnvironment.UNSPECIFIED) {
				configEntry.setEnvironment(environment);
			}
		}
		return this;
	}

	@Override
	public ConfigCategory setScope(@NotNull ReloadScope scope) {
		super.setScope(scope);
		for (ConfigEntry<?> configEntry : entries.values()) {
			if (configEntry.getScope() == ReloadScope.UNSPECIFIED) {
				configEntry.setScope(scope);
			}
		}
		return this;
	}

	@Override
	public ReloadScope getScope() {
		// In this context this finds the highest scope (the scope that trigger most of the other scopes).
		if (entries.isEmpty()) return getOwnScope();

		ReloadScope highest = getOwnScope() == ReloadScope.UNSPECIFIED ? ReloadScope.SMALLEST : getOwnScope();
		for (ConfigEntry<?> entry : this.entries.values()) {
			ReloadScope entryScope = entry.getScope();
			if (entryScope == ReloadScope.UNSPECIFIED) {
				return ReloadScope.UNSPECIFIED;
			}
			if (entryScope != highest && entryScope.triggers(highest)) {
				highest = entryScope;
			} else if (!highest.triggers(entryScope)) {
				highest = ReloadScope.HIGHEST;
				break;
			}
		}
		return highest;
	}

	@Override
	public boolean matches(ReloadEnvironment environment, ReloadScope scope) {
		for (ConfigEntry<?> entry : entries.values()) {
			if (entry.matches(environment, scope)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <V> void read(@NotNull DataSerializer<V> serializer, @NotNull V value, @NotNull ReloadContext context) throws ConfigReadException {
		DataObject<V> dataObject = serializer.toObject(value);
		entryStream(context).filter(entry -> dataObject.has(entry.getKey())).forEach(entry -> {
			V fieldValue = dataObject.get(entry.getKey());
			try {
				entry.getValue().read(serializer, fieldValue, context);
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
	public void read(@NotNull PacketByteBuf buf, @NotNull ReloadContext context) {
		while (buf.readBoolean()) {
			ConfigEntry<?> entry = entries.get(buf.readString(32767));
			if (entry != null) {
				entry.read(buf, context);
			} else {
				throw new RuntimeException("Attempt to sync unknown entry! Aborting.");
			}
		}
		onReload();
	}

	@Override
	public void write(@NotNull PacketByteBuf buf, @NotNull ReloadContext context) {
		entryStream(context).forEach(entry -> {
			buf.writeBoolean(true);
			buf.writeString(entry.getKey());
			entry.getValue().write(buf, context);
		});
		buf.writeBoolean(false);
	}

	@Override
	public <V> AnnotatedDataValue<Object> write(@NotNull DataSerializer<V> serializer, @Nullable AnnotatedDataValue<V> oldValue, @NotNull ReloadContext context) {
		DataObject<V> object;
		if (oldValue != null && oldValue.getValue() instanceof DataObject) {
			//noinspection unchecked
			object = (DataObject<V>) oldValue.getValue();
		} else {
			object = serializer.newObject();
		}
		entryStream(context).forEach(entry -> {
			AnnotatedDataValue<?> value = entry.getValue().write(serializer, AnnotatedDataValue.of(object.get(entry.getKey())), context);
			if (value != null) {
				object.putRaw(entry.getKey(), value.getValue());
			}
		});
		return AnnotatedDataValue.of(object, comment.equals("") ? null : comment);
	}

	public Stream<Map.Entry<String, ConfigEntry<?>>> entryStream() {
		return entries.entrySet().stream();
	}

	public Stream<Map.Entry<String, ConfigEntry<?>>> entryStream(ReloadEnvironment environment, ReloadScope scope) {
		return entryStream().filter(entry -> entry.getValue().matches(environment, scope));
	}

	public Stream<Map.Entry<String, ConfigEntry<?>>> entryStream(ReloadContext context) {
		return entryStream(context.getEnvironment(), context.getScope());
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
