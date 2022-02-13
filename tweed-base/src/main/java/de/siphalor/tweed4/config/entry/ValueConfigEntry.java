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

package de.siphalor.tweed4.config.entry;

import de.siphalor.tweed4.config.*;
import de.siphalor.tweed4.config.value.ConfigValue;
import de.siphalor.tweed4.config.value.SimpleConfigValue;
import de.siphalor.tweed4.config.value.serializer.ConfigSerializers;
import de.siphalor.tweed4.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;

/**
 * An entry to register at a {@link ConfigFile} or {@link ConfigCategory}.
 * @param <T> the type which is used for maintaining the value of the entry. Use {@link ValueConfigEntry#currentValue} to access
 */
@SuppressWarnings("unchecked")
public class ValueConfigEntry<T> extends AbstractValueConfigEntry<ValueConfigEntry<T>, T> {
	private ConfigValueSerializer<T> valueSerializer;

	protected T mainConfigValue;

	/**
	 * Constructs a new entry
	 * @param defaultValue The default value to use
	 */
	public ValueConfigEntry(T defaultValue) {
		this(new SimpleConfigValue<>(defaultValue), ConfigSerializers.deduce(defaultValue, (Class<T>) defaultValue.getClass(), null));
	}

	public ValueConfigEntry(T defaultValue, ConfigValueSerializer<T> configValueSerializer) {
		this(new SimpleConfigValue<>(defaultValue), configValueSerializer);
	}

	public ValueConfigEntry(ConfigValue<T> configValue, ConfigValueSerializer<T> valueSerializer) {
		super(configValue);
		this.valueSerializer = valueSerializer;
		this.defaultValue = currentValue.get();
		this.mainConfigValue = defaultValue;
	}

	public ConfigValueSerializer<T> getValueSerializer() {
		return valueSerializer;
	}

	@Override
	public void setMainConfigValue(T mainConfigValue) {
		this.mainConfigValue = mainConfigValue;
	}

	@Override
	public final T getMainConfigValue() {
		return mainConfigValue;
	}

	@Override
	public Class<T> getType() {
		return valueSerializer.getType();
	}

	@Override
	public String asString(T value) {
		return valueSerializer != null ? valueSerializer.asString(value) : value.toString();
	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void read(V dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		currentValue.set(valueSerializer.read(dataValue));
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = currentValue.get();
		}
		onReload();
	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(environment.triggers(getEnvironment())) {
			if(scope.triggers(getScope())) {
				if(origin == ConfigOrigin.MAIN)
					mainConfigValue = valueSerializer.read(buf);
				else
					currentValue.set(valueSerializer.read(buf));
				onReload();
			}
			else if(origin == ConfigOrigin.MAIN)
				mainConfigValue = valueSerializer.read(buf);
		} else {
			valueSerializer.read(buf);
		}
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> void write(DataContainer<Key, V, L, O> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		valueSerializer.write(dataContainer, key, mainConfigValue);
        if(dataContainer.has(key)) dataContainer.get(key).setComment(getDescription());
    }

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(origin == ConfigOrigin.MAIN)
			valueSerializer.write(buf, mainConfigValue);
		else
			valueSerializer.write(buf, currentValue.get());
	}

}
