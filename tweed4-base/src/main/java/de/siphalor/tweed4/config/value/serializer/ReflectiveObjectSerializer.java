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
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ReflectiveObjectSerializer<T> extends ConfigValueSerializer<T> {
	private final Class<T> clazz;
	private final Supplier<T> supplier;
	private final Map<String, Entry> entries;

	protected ReflectiveObjectSerializer(Class<T> clazz, Supplier<T> supplier, Map<String, Entry> entries) {
		this.clazz = clazz;
		this.supplier = supplier;
		this.entries = entries;
	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	T read(V data) throws ConfigReadException {
		if (data.isObject()) {
			O dataObject = data.asObject();
			T object = supplier.get();

			for (Map.Entry<String, Entry> entry : entries.entrySet()) {
				if (Modifier.isFinal(entry.getValue().field.getModifiers())) {
					continue;
				}
				try {
					entry.getValue().field.set(object, entry.getValue().serializer.read(dataObject.get(entry.getKey())));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return object;
		}
		return null;
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, T value) {
		O dataObject = dataContainer.addObject(key);
		for (Map.Entry<String, Entry> entry : entries.entrySet()) {
			try {
				entry.getValue().serializer.write(dataObject, entry.getKey(), entry.getValue().field.get(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public T read(PacketByteBuf packetByteBuf) {
		T object = supplier.get();

		int size = packetByteBuf.readVarInt();
		for (int i = 0; i < size; i++) {
			Entry entry = entries.get(packetByteBuf.readString(32767));
			try {
				entry.field.set(object, entry.serializer.read(packetByteBuf));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, T value) {
		packetByteBuf.writeVarInt(entries.size());
		for (Map.Entry<String, Entry> entry : entries.entrySet()) {
			if (Modifier.isFinal(entry.getValue().field.getModifiers())) {
				continue;
			}
			packetByteBuf.writeString(entry.getKey());
			try {
				entry.getValue().serializer.write(packetByteBuf, entry.getValue().field.get(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public T copy(T value) {
		T copy = supplier.get();
		for (Map.Entry<String, Entry> entry : entries.entrySet()) {
			Entry entry2 = entry.getValue();
			try {
				entry2.field.set(copy, entry2.serializer.copy(entry2.field.get(value)));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return copy;
	}

	@Override
	public String asString(T value) {
		StringBuilder stringBuilder = new StringBuilder(value.getClass().getSimpleName() + " {\n");
		for (Map.Entry<String, Entry> entry : entries.entrySet()) {
			stringBuilder.append(entry.getKey());
			stringBuilder.append(": ");
			try {
				stringBuilder.append(
						Arrays.stream(StringUtils.split(
								entry.getValue().serializer.asString(entry.getValue().field.get(value)), "\n"
						)).map(line -> "\t" + line).collect(Collectors.joining("\n")).trim()
				);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				stringBuilder.append("<I am error>");
			}
			stringBuilder.append(",\n");
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	@Override
	public Class<T> getType() {
		return clazz;
	}

	protected static class Entry {
		protected final Field field;
		protected final ConfigValueSerializer<Object> serializer;

		public Entry(Field field, ConfigValueSerializer<Object> serializer) {
			this.field = field;
			this.serializer = serializer;
		}
	}
}
