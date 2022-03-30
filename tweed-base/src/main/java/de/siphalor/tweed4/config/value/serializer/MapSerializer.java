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

import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.Map;
import java.util.function.Supplier;

public class MapSerializer<MK, MV, M extends Map<MK, MV>> extends ConfigValueSerializer<M> {
	private final Supplier<M> mapSupplier;
	private final ConfigValueSerializer<MK> keySerializer;
	private final ConfigValueSerializer<MV> valueSerializer;

	public MapSerializer(ConfigValueSerializer<MK> keySerializer, ConfigValueSerializer<MV> valueSerializer, Supplier<M> mapSupplier) {
		this.mapSupplier = mapSupplier;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	M read(V data) throws ConfigReadException {
		if (!data.isList()) {
			throw new ConfigReadException("Expected a list, got " + data);
		}

		M map = mapSupplier.get();
		for (V entry : data.asList()) {
			if (!entry.isObject()) {
				throw new ConfigReadException("Expected map entry to be an object, got " + entry);
			}

			O entryObject = entry.asObject();
			MK key = keySerializer.read(entryObject.get("key"));
			MV value = valueSerializer.read(entryObject.get("value"));
			map.put(key, value);
		}

		return map;
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, M value) {
		L list = dataContainer.addList(key);
		int i = 0;
		for (Map.Entry<MK, MV> entry : value.entrySet()) {
			O entryObject = list.addObject(i);
			keySerializer.write(entryObject, "key", entry.getKey());
			valueSerializer.write(entryObject, "value", entry.getValue());
		}
	}

	@Override
	public M read(PacketByteBuf packetByteBuf) {
		int size = packetByteBuf.readVarInt();
		M map = mapSupplier.get();
		for (int i = 0; i < size; i++) {
			MK key = keySerializer.read(packetByteBuf);
			MV value = valueSerializer.read(packetByteBuf);
			map.put(key, value);
		}
		return map;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, M value) {
		packetByteBuf.writeVarInt(value.size());
		for (Map.Entry<MK, MV> entry : value.entrySet()) {
			keySerializer.write(packetByteBuf, entry.getKey());
			valueSerializer.write(packetByteBuf, entry.getValue());
		}
	}

	@Override
	public String asString(M value) {
		StringBuilder stringBuilder = new StringBuilder("{ ");
		for (Map.Entry<MK, MV> entry : value.entrySet()) {
			stringBuilder.append(keySerializer.asString(entry.getKey()));
			stringBuilder.append(": ");
			stringBuilder.append(valueSerializer.asString(entry.getValue()));
			stringBuilder.append(", ");
		}
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	@Override
	public Class<M> getType() {
		return null;
	}
}
