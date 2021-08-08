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

package de.siphalor.tweed4.config.entry;

import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigOrigin;
import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;

public class ConstantConfigEntry<T> extends AbstractBasicEntry<T> {
	private final T value;
	private final ConfigValueSerializer<T> valueSerializer;

	public ConstantConfigEntry(T value, ConfigValueSerializer<T> valueSerializer) {
		this.value = value;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {

	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void read(V dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {

	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {

	}

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {

	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		valueSerializer.write(dataContainer, key, value);
	}

	@Override
	public String getDescription() {
		return getComment();
	}
}
