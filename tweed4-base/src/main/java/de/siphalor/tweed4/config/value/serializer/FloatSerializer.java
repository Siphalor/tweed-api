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

package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class FloatSerializer extends ConfigValueSerializer<Float> {
	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	Float read(V data) {
		if(data.isNumber())
			return data.asFloat();
		return 0F;
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, Float value) {
		dataContainer.set(key, value);
	}

	@Override
	public Float read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Float value) {
		packetByteBuf.writeFloat(value);
	}

	@Override
	public String asString(Float value) {
		return value.toString();
	}

	@Override
	public Class<Float> getType() {
		return Float.class;
	}
}
