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

package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;

public class StringSerializer extends ConfigValueSerializer<String> {
	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	String read(V data) {
		if (data.isString()) {
			return data.asString();
		}
		return "";
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, String value) {
		dataContainer.set(key, value);
	}

	@Override
	public String read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, String value) {
		packetByteBuf.writeString(value);
	}

	@Override
	public String asString(String value) {
		return value;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}
}
