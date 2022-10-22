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

package de.siphalor.tweed5.config.value.serializer;

import de.siphalor.tweed5.config.ConfigReadException;
import de.siphalor.tweed5.data.DataList;
import de.siphalor.tweed5.data.DataObject;
import de.siphalor.tweed5.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;

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
	public <V> M read(DataSerializer<V> serializer, V value) throws ConfigReadException {
		M map = mapSupplier.get();
		for (V element : serializer.toList(value)) {
			DataObject<V> elementObject = serializer.toObject(element);
			MK elementKey = keySerializer.read(serializer, elementObject.get("key"));
			MV elementValue = valueSerializer.read(serializer, elementObject.get("value"));
			map.put(elementKey, elementValue);
		}
		return map;
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, M value) {
		DataList<V> list = serializer.newList();
		for (Map.Entry<MK, MV> entry : value.entrySet()) {
			DataObject<V> elementObject = serializer.newObject();
			elementObject.putRaw("key", keySerializer.write(serializer, entry.getKey()));
			elementObject.putRaw("value", valueSerializer.write(serializer, entry.getValue()));
			list.addRaw(elementObject);
		}
		return list;
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
