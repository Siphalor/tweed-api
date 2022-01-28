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

import java.util.Set;

public interface DataContainer<Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> extends DataValue<V, L, O> {
	boolean has(Key key);
	int size();

	@Override
	default boolean isEmpty() {
		return size() > 0;
	}

	V get(Key key);

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
		V value = get(key);
		if (value != null && value.isByte()) {
			return value.asByte();
		}
		return def;
	}
	default short getShort(Key key, short def) {
		V value = get(key);
		if (value != null && value.isShort()) {
			return value.asShort();
		}
		return def;
	}
	default int getInt(Key key, int def) {
		V value = get(key);
		if (value != null && value.isInt()) {
			return value.asInt();
		}
		return def;
	}
	default long getLong(Key key, long def) {
		V value = get(key);
		if (value != null && value.isLong()) {
			return value.asLong();
		}
		return def;
	}
	default float getFloat(Key key, float def) {
		V value = get(key);
		if (value != null && value.isFloat()) {
			return value.asFloat();
		}
		return def;
	}
	default double getDouble(Key key, double def) {
		V value = get(key);
		if (value != null && value.isDouble()) {
			return value.asDouble();
		}
		return def;
	}
	default char getCharacter(Key key, char def) {
		V value = get(key);
		if (value != null && value.isChar()) {
			return value.asChar();
		}
		return def;
	}
	default String getString(Key key, String def) {
		V value = get(key);
		if (value != null && value.isString()) {
			return value.asString();
		}
		return def;
	}
	default boolean getBoolean(Key key, boolean def) {
		V value = get(key);
		if (value != null && value.isBoolean()) {
			return value.asBoolean();
		}
		return def;
	}
	default O getObject(Key key, O def) {
		V value = get(key);
		if (value != null && value.isObject()) {
			return value.asObject();
		}
		return def;
	}
	default L getByte(Key key, L def) {
		V value = get(key);
		if (value != null && value.isList()) {
			return value.asList();
		}
		return def;
	}

	V set(Key key, byte value);
	V set(Key key, short value);
	V set(Key key, int value);
	V set(Key key, long value);
	V set(Key key, float value);
	V set(Key key, double value);
	V set(Key key, char value);
	V set(Key key, String value);
	V set(Key key, boolean value);
	V set(Key key, V value);

	O addObject(Key key);
	L addList(Key key);
	/**
	 * Creates a new null representation with the given key. <br />
	 * <i>This should always be overridden. The default is only here for legacy reasons.</i>
	 * @param key The key to use.
	 * @return A new null representation. <code>null</code> indicates not supporting null values.
	 * @since 1.2
	 */
	default V addNull(Key key) {
		return null;
	}

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
	default boolean isChar() {
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
	default char asChar() {
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
}
