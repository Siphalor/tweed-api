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

package de.siphalor.tweed5.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class EnumRepresentation<T extends EnumRepresentation.EnumLike> {
	private final Map<String, T> map;
	private final SortedSet<T> values;

	protected EnumRepresentation(Comparator<T> comparator) {
		map = new HashMap<>();
		values = new TreeSet<>(comparator);
	}

	public static <T extends EnumLike> EnumRepresentation<T> create() {
		return create(Comparator.comparing(EnumLike::name));
	}

	public static <T extends EnumLike> EnumRepresentation<T> create(Comparator<T> comparator) {
		return new EnumRepresentation<>(comparator);
	}

	public static <T extends EnumLike> EnumRepresentation<T> fromConstants(Class<T> clazz) {
		return fromConstants(clazz, Comparator.comparing(EnumLike::name));
	}

	public static <T extends EnumLike> EnumRepresentation<T> fromConstants(Class<T> clazz, Comparator<T> comparator) {
		EnumRepresentation<T> enumRepresentation = new EnumRepresentation<>(comparator);
		for (Field field : clazz.getFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && clazz.isAssignableFrom(field.getType())) {
				try {
					//noinspection unchecked
					enumRepresentation.register((T) field.get(null));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return enumRepresentation;
	}

	public T valueOf(String name) {
		T value = map.get(name.toLowerCase(Locale.ROOT));
		if (value == null) {
			throw new IllegalArgumentException("No enum constant " + name);
		}
		return value;
	}

	public void register(T value) {
		map.put(value.name().toLowerCase(Locale.ROOT), value);
		values.add(value);
	}

	public Collection<T> values() {
		return values;
	}

	public interface EnumLike {
		String name();
	}
}
