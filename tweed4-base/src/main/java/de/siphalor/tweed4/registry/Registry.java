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

package de.siphalor.tweed4.registry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class Registry<K, T> {
	private final Map<K, T> entries;

	public Registry() {
		this.entries = new HashMap<>();
	}

	public T register(K key, T value) {
		if (entries.containsKey(key) || entries.containsValue(value)) {
			throw new IllegalArgumentException("Duplicate registry entry " + key);
		}
		entries.put(key, value);
		return value;
	}

	public T get(K key) {
		return entries.get(key);
	}

	public boolean containsKey(K key) {
		return entries.containsKey(key);
	}

	public Set<Map.Entry<K, T>> getEntries() {
		return Collections.unmodifiableSet(entries.entrySet());
	}

	public Set<K> getKeys() {
		return Collections.unmodifiableSet(entries.keySet());
	}

	public Collection<T> getValues() {
		return Collections.unmodifiableCollection(entries.values());
	}
}
