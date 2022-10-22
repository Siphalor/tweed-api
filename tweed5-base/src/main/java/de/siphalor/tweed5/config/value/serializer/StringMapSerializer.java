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
import de.siphalor.tweed5.data.DataObject;
import de.siphalor.tweed5.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.Map;
import java.util.function.Supplier;

public class StringMapSerializer<MV, M extends Map<String, MV>> extends ConfigValueSerializer<M> {
	private final Supplier<M> mapSupplier;
	private final ConfigValueSerializer<MV> valueSerializer;

	public StringMapSerializer(ConfigValueSerializer<MV> valueSerializer, Supplier<M> mapSupplier) {
		this.mapSupplier = mapSupplier;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public <V> M read(DataSerializer<V> serializer, V value) throws ConfigReadException {
		M map = mapSupplier.get();
		for (Map.Entry<String, V> entry : serializer.toObject(value).entrySet()) {
			map.put(entry.getKey(), valueSerializer.read(serializer, entry.getValue()));
		}
		return map;
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, M value) {
		DataObject<V> object = serializer.newObject();
		for (Map.Entry<String, MV> entry : value.entrySet()) {
			object.putRaw(entry.getKey(), valueSerializer.write(serializer, entry.getValue()));
		}
		return object;
	}

	@Override
	public M read(PacketByteBuf packetByteBuf) {
		M map = mapSupplier.get();
		int size = packetByteBuf.readVarInt();
		for (int i = 0; i < size; i++) {
			String key = packetByteBuf.readString(Short.MAX_VALUE);
			MV value = valueSerializer.read(packetByteBuf);
			map.put(key, value);
		}
		return map;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, M value) {
		packetByteBuf.writeVarInt(value.size());
		for (Map.Entry<String, MV> entry : value.entrySet()) {
			packetByteBuf.writeString(entry.getKey());
			valueSerializer.write(packetByteBuf, entry.getValue());
		}
	}

	@Override
	public String asString(M value) {
		StringBuilder stringBuilder = new StringBuilder("{ ");
		for (Map.Entry<String, MV> entry : value.entrySet()) {
			stringBuilder.append(entry.getKey()).append(": ").append(valueSerializer.asString(entry.getValue())).append(", ");
		}
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}

	@Override
	public Class<M> getType() {
		//noinspection unchecked
		return (Class<M>) mapSupplier.get().getClass();
	}
}
