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
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.List;
import java.util.function.Supplier;

public class ListSerializer<E, L extends List<E>> extends ConfigValueSerializer<L> {
	ConfigValueSerializer<E> valueSerializer;
	Supplier<L> listSupplier;

	public ListSerializer(ConfigValueSerializer<E> elementSerializer, Supplier<L> listSupplier) {
		this.valueSerializer = elementSerializer;
		this.listSupplier = listSupplier;
	}

	@Override
	public L read(DataValue<?> data) throws ConfigReadException {
		L list = listSupplier.get();
		if (data.isList()) {
			DataList<?> dataList = data.asList();
			for (DataValue<?> dataValue : dataList) {
				list.add(valueSerializer.read(dataValue));
			}
		}
		return list;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, L value) {
		DataList<?> dataList = dataContainer.addList(key);
		int i = 0;
		for (E element : value) {
			valueSerializer.write(dataList, i, element);
		}
	}

	@Override
	public L read(PacketByteBuf packetByteBuf) {
		int l = packetByteBuf.readVarInt();
		L list = listSupplier.get();
		for (int i = 0; i < l; i++) {
			list.add(valueSerializer.read(packetByteBuf));
		}
		return list;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, L value) {
		packetByteBuf.writeVarInt(value.size());
		for (E element : value) {
			valueSerializer.write(packetByteBuf, element);
		}
	}

	@Override
	public String asString(L value) {
		StringBuilder stringBuilder = new StringBuilder("[ ");
		for (E element : value) {
			stringBuilder.append(valueSerializer.asString(element)).append(", ");
		}
		return stringBuilder.append(" ]").toString();
	}

	@Override
	public Class<L> getType() {
		return (Class<L>) listSupplier.get().getClass();
	}
}
