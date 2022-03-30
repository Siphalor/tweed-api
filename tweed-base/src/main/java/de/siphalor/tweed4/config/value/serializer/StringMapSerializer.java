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

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
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
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> M
	read(V data) throws ConfigReadException {
		if (!data.isObject()) {
			throw new ConfigReadException("Expected object, got " + data);
		}

		M map = mapSupplier.get();
		for (Pair<String, V> pair : data.asObject()) {
			map.put(pair.getFirst(), valueSerializer.read(pair.getSecond()));
		}
		return map;
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, M value) {
		O object = dataContainer.addObject(key);
		for (Map.Entry<String, MV> entry : value.entrySet()) {
			valueSerializer.write(object, entry.getKey(), entry.getValue());
		}
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
