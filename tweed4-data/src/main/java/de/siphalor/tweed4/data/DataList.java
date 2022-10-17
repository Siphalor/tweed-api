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

import java.util.List;

public interface DataList<V, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> extends List<V> {
	V getValue();

	String getComment(int index);
	void setComment(int index, String comment);

	default Object getRaw(int index) {
		return getSerializer().toRaw(get(index), null);
	}

	default void add(V value, String comment) {
		add(value);
		setComment(size() - 1, comment);
	}
	default void add(AnnotatedDataValue<V> value) {
		add(value.getValue(), value.getComment());
	}
	default void add(int index, V value, String comment) {
		add(index, value);
		setComment(index, comment);
	}
	default void add(int index, AnnotatedDataValue<V> value) {
		add(index, value.getValue(), value.getComment());
	}
	default void addRaw(Object raw) {
		add(getSerializer().fromRawPrimitive(raw));
	}
	default void addRaw(int index, Object raw) {
		add(index, getSerializer().fromRawPrimitive(raw));
	}
	default void addRaw(Object raw, String comment) {
		add(getSerializer().fromRawPrimitive(raw), comment);
	}
	default void addRaw(AnnotatedDataValue<Object> value) {
		addRaw(value.getValue(), value.getComment());
	}
	default void addRaw(int index, Object raw, String comment) {
		add(index, getSerializer().fromRawPrimitive(raw), comment);
	}
	default void addRaw(int index, AnnotatedDataValue<Object> value) {
		addRaw(index, value.getValue(), value.getComment());
	}

	default Object set(int index, V value, String comment) {
		Object old = set(index, value);
		setComment(index, comment);
		return old;
	}
	default Object set(int index, AnnotatedDataValue<V> value) {
		return set(index, value.getValue(), value.getComment());
	}
	default Object setRaw(int index, Object raw) {
		return set(index, getSerializer().fromRawPrimitive(raw));
	}
	default Object setRaw(int index, Object raw, String comment) {
		return set(index, getSerializer().fromRawPrimitive(raw), comment);
	}
	default Object setRaw(int index, AnnotatedDataValue<Object> value) {
		return setRaw(index, value.getValue(), value.getComment());
	}

	DataSerializer<V, L, O> getSerializer();
}
