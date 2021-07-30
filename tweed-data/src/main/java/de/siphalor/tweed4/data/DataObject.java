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

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface DataObject<V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> extends Iterable<Pair<String, V>>, DataContainer<String, V, L, O> {
	@Override
	boolean has(String key);

	@Override
	void remove(String key);

	@Override
	V set(String key, V value);

	@Override
	V set(String key, boolean value);

	@Override
	V set(String key, String value);

	@Override
	V set(String key, char value);

	@Override
	V set(String key, double value);

	@Override
	V set(String key, float value);

	@Override
	V set(String key, long value);

	@Override
	V set(String key, int value);

	@Override
	V set(String key, short value);

	@Override
	V set(String key, byte value);

	@Override
	O addObject(String key);

	@Override
	L addList(String key);

	@Override
	V get(String key);

	@Override
	default boolean isObject() {
		return true;
	}

	@Override
	default boolean isList() {
		return false;
	}

	@Override
	default O asObject() {
		//noinspection unchecked
		return (O) this;
	}

	@Override
	default L asList() {
		return null;
	}

	@Override
	default Set<String> keys() {
		//noinspection UnstableApiUsage
		return Streams.stream(iterator()).map(Pair::getFirst).collect(Collectors.toSet());
	}

	/**
	 * @deprecated Please override {@link DataObject#keys()} as well.
	 * This method will at some point be removed in favor of {@link DataObject#keys()}.
	 */
	@NotNull
	@Deprecated
	@ApiStatus.OverrideOnly
	Iterator<Pair<String, V>> iterator();

	@Deprecated
	default Spliterator<Pair<String, V>> spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(), 0);
	}

	@Deprecated
	default void forEach(Consumer<? super Pair<String, V>> action) {
		Objects.requireNonNull(action);
		for (Pair<String, V> pair : this) {
			action.accept(pair);
		}
	}
}
