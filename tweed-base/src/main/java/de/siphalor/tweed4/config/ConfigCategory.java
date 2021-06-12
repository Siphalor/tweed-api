/*
 * Copyright 2021 Siphalor
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
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;

import java.util.Iterator;
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
		if(configEntry.getEnvironment() == ConfigEnvironment.DEFAULT) configEntry.setEnvironment(environment);
		if(configEntry.getScope() == ConfigScope.DEFAULT) configEntry.setScope(scope);
		return configEntry;
	}

	public ConfigEntry<?> get(String name) {
		return entries.get(name);
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
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
	public ConfigCategory setEnvironment(ConfigEnvironment environment) {
		super.setEnvironment(environment);
        entries.values().stream().filter(configEntry -> configEntry.getEnvironment() == ConfigEnvironment.DEFAULT).forEach(configEntry -> configEntry.setEnvironment(environment));
		return this;
	}

	@Override
	public ConfigEnvironment getEnvironment() {
		if(entries.isEmpty()) return environment;
		Iterator<ConfigEntry<?>> iterator = entries.values().iterator();
		ConfigEnvironment environment = iterator.next().getEnvironment();
		while(iterator.hasNext()) {
			ConfigEnvironment itEnvironment = iterator.next().getEnvironment();
            while(!environment.contains(itEnvironment))
            	environment = environment.parent;
		}
		return environment;
	}

	@Override
	public ConfigCategory setScope(ConfigScope scope) {
		super.setScope(scope);
		entries.values().stream().filter(configEntry -> configEntry.getScope() == ConfigScope.DEFAULT).forEach(configEntry -> configEntry.setScope(scope));
		return this;
	}

	@Override
	public ConfigScope getScope() {
		if(entries.isEmpty()) return scope;
		return entries.values().stream().map(ConfigEntry::getScope).min((o1, o2) -> o1 == o2 ? 0 : (o1.triggers(o2) ? -1 : 1)).get();
	}

	@Override
	public void read(DataValue<?> dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		if(!dataValue.isObject()) {
			throw new ConfigReadException("The entry should be an object (category)");
		}
		DataObject<?> dataObject = dataValue.asObject();
		entryStream(environment, scope).filter(entry -> dataObject.has(entry.getKey())).forEach(entry -> {
			DataValue<?> value = dataObject.get(entry.getKey());
			try {
				entry.getValue().read(value, environment, scope, origin);
			} catch (ConfigReadException e) {
				Tweed.LOGGER.error("Error reading " + entry.getKey() + ":");
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
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		while(buf.readBoolean()) {
			ConfigEntry<?> entry = entries.get(buf.readString(32767));
			if(entry != null)
				entry.read(buf, environment, scope, origin);
			else
				throw new RuntimeException("Attempt to sync unknown entry! Aborting.");
		}
		onReload();
	}

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		entryStream(environment, scope).forEach(entry -> {
			buf.writeBoolean(true);
			buf.writeString(entry.getKey());
			entry.getValue().write(buf, environment, scope, origin);
		});
		buf.writeBoolean(false);
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		DataContainer category;
		if(key.equals("")) {
			category = dataContainer;
		} else if(!dataContainer.has(key)) {
            category = dataContainer.addObject(key);
		} else {
			category = dataContainer.get(key).asObject();
		}
		if(!comment.equals(""))
			category.setComment(getComment());
		entryStream(environment, scope).forEach(entry -> entry.getValue().write(category, entry.getKey(), environment, scope));
	}

	public Stream<Map.Entry<String, ConfigEntry<?>>> entryStream() {
		return entries.entrySet().stream();
	}

	public Stream<Map.Entry<String, ConfigEntry<?>>> entryStream(ConfigEnvironment environment, ConfigScope scope) {
		return entryStream().filter(entry -> environment.triggers(entry.getValue().getEnvironment()) && scope.triggers(entry.getValue().getScope()));
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
