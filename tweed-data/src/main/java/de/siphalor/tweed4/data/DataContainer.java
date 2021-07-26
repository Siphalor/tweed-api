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

package de.siphalor.tweed4.data;

import de.siphalor.tweed4.data.serializer.DataSerializer;

import java.util.Set;

public interface DataContainer<RawValue, Key> extends DataValue<RawValue> {
	boolean has(Key key);
	int size();

	DataValue<RawValue> get(Key key);

	default boolean hasByte(Key key) {
		return has(key) && get(key).isByte();
	}
	default boolean hasShort(Key key) {
		return has(key) && get(key).isShort();
	}
	default boolean hasInt(Key key) {
		return has(key) && get(key).isInt();
	}
	default boolean hasLong(Key key) {
		return has(key) && get(key).isLong();
	}
	default boolean hasFloat(Key key) {
		return has(key) && get(key).isFloat();
	}
	default boolean hasDouble(Key key) {
		return has(key) && get(key).isDouble();
	}
	default boolean hasCharacter(Key key) {
		return has(key) && get(key).isChar();
	}
	default boolean hasString(Key key) {
		return has(key) && get(key).isString();
	}
	default boolean hasBoolean(Key key) {
		return has(key) && get(key).isBoolean();
	}
	default boolean hasObject(Key key) {
		return has(key) && get(key).isObject();
	}
	default boolean hasList(Key key) {
		return has(key) && get(key).isList();
	}

	default byte getByte(Key key, byte def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isByte()) {
			return value.asByte();
		}
		return def;
	}
	default short getShort(Key key, short def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isShort()) {
			return value.asShort();
		}
		return def;
	}
	default int getInt(Key key, int def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isInt()) {
			return value.asInt();
		}
		return def;
	}
	default long getLong(Key key, long def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isLong()) {
			return value.asLong();
		}
		return def;
	}
	default float getFloat(Key key, float def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isFloat()) {
			return value.asFloat();
		}
		return def;
	}
	default double getDouble(Key key, double def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isDouble()) {
			return value.asDouble();
		}
		return def;
	}
	default char getCharacter(Key key, char def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isChar()) {
			return value.asChar();
		}
		return def;
	}
	default String getString(Key key, String def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isString()) {
			return value.asString();
		}
		return def;
	}
	default boolean getBoolean(Key key, boolean def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isBoolean()) {
			return value.asBoolean();
		}
		return def;
	}
	default DataObject<RawValue> getObject(Key key, DataObject<RawValue> def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isObject()) {
			return value.asObject();
		}
		return def;
	}
	default DataList<RawValue> getByte(Key key, DataList<RawValue> def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isList()) {
			return value.asList();
		}
		return def;
	}

	DataValue<RawValue> set(Key key, byte value);
	DataValue<RawValue> set(Key key, short value);
	DataValue<RawValue> set(Key key, int value);
	DataValue<RawValue> set(Key key, long value);
	DataValue<RawValue> set(Key key, float value);
	DataValue<RawValue> set(Key key, double value);
	DataValue<RawValue> set(Key key, char value);
	DataValue<RawValue> set(Key key, String value);
	DataValue<RawValue> set(Key key, boolean value);
	DataValue<RawValue> set(Key key, DataValue<RawValue> value);

	DataObject<RawValue> addObject(Key key);
	DataList<RawValue> addList(Key key);

	@Override
	default boolean isGenericNumber() {
		return false;
	}

	@Override
	default boolean isNumber() {
		return false;
	}

	@Override
	default boolean isByte() {
		return false;
	}

	@Override
	default boolean isShort() {
		return false;
	}

	@Override
	default boolean isInt() {
		return false;
	}

	@Override
	default boolean isLong() {
		return false;
	}

	@Override
	default boolean isFloat() {
		return false;
	}

	@Override
	default boolean isDouble() {
		return false;
	}

	@Override
	default boolean isString() {
		return false;
	}

	@Override
	default boolean isBoolean() {
		return false;
	}

	@Override
	default Number asNumber() {
		return 0;
	}

	@Override
	default byte asByte() {
		return 0;
	}

	@Override
	default short asShort() {
		return 0;
	}

	@Override
	default int asInt() {
		return 0;
	}

	@Override
	default long asLong() {
		return 0;
	}

	@Override
	default float asFloat() {
		return 0;
	}

	@Override
	default double asDouble() {
		return 0;
	}

	@Override
	default String asString() {
		return "";
	}

	@Override
	default boolean asBoolean() {
		return !isEmpty();
	}

	Set<Key> keys();

	void remove(Key key);

	@SuppressWarnings("unchecked")
	default <Other> DataContainer<Other, Key> convert(DataSerializer<Other> serializer) {
		DataContainer<Other, Key> other;
		if (this instanceof DataList) {
			other = (DataContainer<Other, Key>) serializer.newList();
		} else if (this instanceof DataObject) {
			other = (DataContainer<Other, Key>) serializer.newObject();
		} else {
			throw new RuntimeException("Unknown data type " + this.getClass().getTypeName());
		}

		for (Key key : keys()) {
			DataValue<RawValue> dataValue = get(key);
			if (dataValue.isChar()) {
				other.set(key, dataValue.asChar());
			} else if (dataValue.isString()) {
				other.set(key, dataValue.asString());
			} else if (dataValue.isBoolean()) {
				other.set(key, dataValue.asBoolean());
			} else if (dataValue.isGenericNumber()) {
				Number number = dataValue.asNumber();
				if (number instanceof Byte) {
					other.set(key, number.byteValue());
				} else if (number instanceof Short) {
					other.set(key, number.shortValue());
				} else if (number instanceof Integer) {
					other.set(key, number.intValue());
				} else if (number instanceof Long) {
					other.set(key, number.longValue());
				} else if (number instanceof Float) {
					other.set(key, number.floatValue());
				} else {
					other.set(key, number.doubleValue());
				}
			} else if (dataValue.isByte()) {
				other.set(key, dataValue.asByte());
			} else if (dataValue.isShort()) {
				other.set(key, dataValue.asShort());
			} else if (dataValue.isInt()) {
				other.set(key, dataValue.asInt());
			} else if (dataValue.isLong()) {
				other.set(key, dataValue.asLong());
			} else if (dataValue.isFloat()) {
				other.set(key, dataValue.asFloat());
			} else if (dataValue.isDouble()) {
				other.set(key, dataValue.asDouble());
			} else if (dataValue.isList()) {
				other.set(key, dataValue.asList().convert(serializer));
			} else if (dataValue.isObject()) {
				other.set(key, dataValue.asObject().convert(serializer));
			}
		}

		return other;
	}
}
