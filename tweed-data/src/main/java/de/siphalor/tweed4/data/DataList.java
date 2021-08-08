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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface DataList<V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> extends Iterable<V>, DataContainer<Integer, V, L, O> {
	@Override
	default boolean has(Integer index) {
		return index < size();
	}

	@Override
	void remove(Integer index);

	@Override
	V get(Integer index);

	@Override
	V set(Integer index, byte value);

	@Override
	V set(Integer index, short value);

	@Override
	V set(Integer index, int value);

	@Override
	V set(Integer index, long value);

	@Override
	V set(Integer index, float value);

	@Override
	V set(Integer index, double value);

	@Override
	V set(Integer index, char value);

	@Override
	V set(Integer index, String value);

	@Override
	V set(Integer index, boolean value);

	@Override
	V set(Integer index, V value);

	@Override
	L addList(Integer index);

	@Override
	O addObject(Integer index);

	@Override
	default boolean isObject() {
		return false;
	}

	@Override
	default boolean isList() {
		return true;
	}

	@Override
	default O asObject() {
		return null;
	}

	@Override
	default L asList() {
		//noinspection unchecked
		return (L) this;
	}

	@Override
	default Set<Integer> keys() {
		return IntStream.range(0, size()).boxed().collect(Collectors.toSet());
	}
}
