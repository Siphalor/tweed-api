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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface DataList<RawValue> extends Iterable<DataValue<RawValue>>, DataContainer<RawValue, Integer> {
	@Override
	default boolean has(Integer index) {
		return index < size();
	}

	@Override
	void remove(Integer index);

	@Override
	DataValue<RawValue> get(Integer index);

	@Override
	DataValue<RawValue> set(Integer index, byte value);

	@Override
	DataValue<RawValue> set(Integer index, short value);

	@Override
	DataValue<RawValue> set(Integer index, int value);

	@Override
	DataValue<RawValue> set(Integer index, long value);

	@Override
	DataValue<RawValue> set(Integer index, float value);

	@Override
	DataValue<RawValue> set(Integer index, double value);

	@Override
	DataValue<RawValue> set(Integer index, char value);

	@Override
	DataValue<RawValue> set(Integer index, String value);

	@Override
	DataValue<RawValue> set(Integer index, boolean value);

	@Override
	DataValue<RawValue> set(Integer index, DataValue<RawValue> value);

	@Override
	DataList<RawValue> addList(Integer index);

	@Override
	DataObject<RawValue> addObject(Integer index);

	@Override
	default boolean isObject() {
		return false;
	}

	@Override
	default boolean isList() {
		return true;
	}

	@Override
	default DataObject<RawValue> asObject() {
		return null;
	}

	@Override
	default DataList<RawValue> asList() {
		return this;
	}

	@Override
	default Set<Integer> keys() {
		return IntStream.range(0, size()).boxed().collect(Collectors.toSet());
	}

	@Override
	default <Other> DataList<Other> convert(DataSerializer<Other> serializer) {
		return (DataList<Other>) DataContainer.super.convert(serializer);
	}
}
