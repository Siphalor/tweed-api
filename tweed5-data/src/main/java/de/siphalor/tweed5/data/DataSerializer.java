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

package de.siphalor.tweed5.data;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface DataSerializer<V> {
	AnnotatedDataValue<V> read(InputStream inputStream);

	void write(OutputStream outputStream, AnnotatedDataValue<V> value);

	default Object toRaw(V value) {
		return toRaw(value, null);
	}
	Object toRaw(V value, @Nullable DataType typeHint);
	default boolean toBoolean(V value) {
		return (boolean) toRaw(value, DataType.BOOLEAN);
	}
	default byte toByte(V value) {
		return (byte) toRaw(value, DataType.BYTE);
	}
	default short toShort(V value) {
		return (short) toRaw(value, DataType.SHORT);
	}
	default int toInt(V value) {
		return (int) toRaw(value, DataType.INTEGER);
	}
	default long toLong(V value) {
		return (long) toRaw(value, DataType.LONG);
	}
	default float toFloat(V value) {
		return (float) toRaw(value, DataType.FLOAT);
	}
	default double toDouble(V value) {
		return (double) toRaw(value, DataType.DOUBLE);
	}
	default String toString(V value) {
		return (String) toRaw(value, DataType.STRING);
	}
	default DataList<V> toList(V value) {
		//noinspection unchecked
		return (DataList<V>) toRaw(value, DataType.LIST);
	}
	default DataObject<V> toObject(V value) {
		//noinspection unchecked
		return (DataObject) toRaw(value, DataType.OBJECT);
	}
	default V fromRaw(Object raw) {
		if (raw instanceof List) {
			return fromRawList((List<?>) raw).getValue();
		} else if (raw instanceof Map) {
			//noinspection unchecked
			return fromRawMap(((Map<String, ?>) raw)).getValue();
		} else {
			return fromRawPrimitive(raw);
		}
	}
	V fromRawPrimitive(Object raw);
	default <RawValue> DataList<V> fromRawList(List<RawValue> list) {
		DataList<V> newList = newList();
		for (RawValue object : list) {
			newList.add(fromRaw(object));
		}
		return newList;
	}
	default <RawValue> DataObject<V> fromRawMap(Map<String, RawValue> map) {
		DataObject<V> newObject = newObject();
		for (Map.Entry<String, RawValue> entry : map.entrySet()) {
			newObject.put(entry.getKey(), fromRawPrimitive(entry.getValue()));
		}
		return newObject;
	}

	DataObject<V> newObject();
	DataList<V> newList();

	String getId();

	String getFileExtension();
}
