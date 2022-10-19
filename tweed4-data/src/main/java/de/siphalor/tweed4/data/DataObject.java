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

package de.siphalor.tweed4.data;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface DataObject<V> extends Map<String, V> {
	@NotNull V getValue();

	String getComment(String key);
	void setComment(String key, String comment);

	boolean has(String key);
	@Override
	default boolean containsKey(Object key) {
		return has((String) key);
	}

	default Object getRaw(String key) {
		return getSerializer().toRaw(get(key), null);
	}

	default Object put(String key, V value, String comment) {
		Object old = put(key, value);
		setComment(key, comment);
		return old;
	}
	default Object put(String key, AnnotatedDataValue<V> value) {
		return put(key, value.getValue(), value.getComment());
	}
	default Object putRaw(String key, Object raw) {
		return put(key, getSerializer().fromRawPrimitive(raw));
	}
	default Object putRaw(String key, Object raw, String comment) {
		return put(key, getSerializer().fromRawPrimitive(raw), comment);
	}
	default Object putRaw(String key, AnnotatedDataValue<Object> value) {
		return putRaw(key, value.getValue(), value.getComment());
	}

	DataSerializer<V> getSerializer();
}
