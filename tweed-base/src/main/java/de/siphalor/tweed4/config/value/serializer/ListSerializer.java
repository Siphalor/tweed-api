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

import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

public class ListSerializer<E, T extends List<E>> extends ConfigValueSerializer<T> {
	ConfigValueSerializer<E> valueSerializer;
	Supplier<T> listSupplier;

	@ApiStatus.Internal
	public ListSerializer(ConfigValueSerializer<E> elementSerializer, Supplier<T> listSupplier) {
		this.valueSerializer = elementSerializer;
		this.listSupplier = listSupplier;
	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	T read(V data) throws ConfigReadException {
		T list = listSupplier.get();
		if (data.isList()) {
			L dataList = data.asList();
			for (V dataValue : dataList) {
				list.add(valueSerializer.read(dataValue));
			}
		}
		return list;
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, T value) {
		DataList<?, ?, ?> dataList = dataContainer.addList(key);
		int i = 0;
		for (E element : value) {
			valueSerializer.write(dataList, i++, element);
		}
	}

	@Override
	public T read(PacketByteBuf packetByteBuf) {
		int l = packetByteBuf.readVarInt();
		T list = listSupplier.get();
		for (int i = 0; i < l; i++) {
			list.add(valueSerializer.read(packetByteBuf));
		}
		return list;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, T value) {
		packetByteBuf.writeVarInt(value.size());
		for (E element : value) {
			valueSerializer.write(packetByteBuf, element);
		}
	}

	@Override
	public T copy(T value) {
		T newList = listSupplier.get();
		for (E element : value) {
			newList.add(valueSerializer.copy(element));
		}
		return newList;
	}

	@Override
	public String asString(T value) {
		StringBuilder stringBuilder = new StringBuilder("[ ");
		for (E element : value) {
			stringBuilder.append(valueSerializer.asString(element)).append(", ");
		}
		return stringBuilder.append(" ]").toString();
	}

	@Override
	public Class<T> getType() {
		//noinspection unchecked
		return (Class<T>) listSupplier.get().getClass();
	}
}
