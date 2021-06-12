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

import com.mojang.datafixers.util.Pair;

public interface DataObject<RawValue> extends Iterable<Pair<String, DataValue<RawValue>>>, DataContainer<RawValue, String> {
	@Override
	boolean has(String key);

	@Override
	DataValue<RawValue> set(String key, DataValue<RawValue> value);

	@Override
	DataValue<RawValue> set(String key, boolean value);

	@Override
	DataValue<RawValue> set(String key, String value);

	@Override
	DataValue<RawValue> set(String key, char value);

	@Override
	DataValue<RawValue> set(String key, double value);

	@Override
	DataValue<RawValue> set(String key, float value);

	@Override
	DataValue<RawValue> set(String key, long value);

	@Override
	DataValue<RawValue> set(String key, int value);

	@Override
	DataValue<RawValue> set(String key, short value);

	@Override
	DataValue<RawValue> set(String key, byte value);

	@Override
	DataObject<RawValue> addObject(String key);

	@Override
	DataList<RawValue> addList(String key);

	@Override
	DataValue<RawValue> get(String key);

	@Override
	void remove(String key);

	@Override
	default boolean isNumber() {
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
	default boolean isObject() {
		return true;
	}

	@Override
	default boolean isList() {
		return false;
	}

	@Override
	default int asInt() {
		return 0;
	}

	@Override
	default float asFloat() {
		return 0;
	}

	@Override
	default boolean asBoolean() {
		return !isEmpty();
	}

	@Override
	default DataObject<RawValue> asObject() {
		return this;
	}

	@Override
	default DataList<RawValue> asList() {
		return null;
	}
}
