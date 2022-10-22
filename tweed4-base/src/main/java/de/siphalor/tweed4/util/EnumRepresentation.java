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

package de.siphalor.tweed4.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class EnumRepresentation<T extends EnumRepresentation.EnumLike> {
	private final Map<String, T> map;
	private final SortedSet<T> values;
	private final T fallback;

	protected EnumRepresentation(T fallback, Comparator<T> comparator) {
		this.fallback = fallback;
		map = new HashMap<>();
		values = new TreeSet<>(comparator);
	}

	public static <T extends EnumLike> EnumRepresentation<T> create(T fallback) {
		return create(fallback, Comparator.comparing(EnumLike::name));
	}

	public static <T extends EnumLike> EnumRepresentation<T> create(T fallback, Comparator<T> comparator) {
		EnumRepresentation<T> enumRepresentation = new EnumRepresentation<>(fallback, comparator);
		enumRepresentation.register(fallback);
		return enumRepresentation;
	}

	public static <T extends EnumLike> EnumRepresentation<T> fromConstants(T fallback, Class<T> clazz) {
		return fromConstants(fallback, clazz, Comparator.comparing(EnumLike::name));
	}

	public static <T extends EnumLike> EnumRepresentation<T> fromConstants(T fallback, Class<T> clazz, Comparator<T> comparator) {
		EnumRepresentation<T> enumRepresentation = new EnumRepresentation<>(fallback, comparator);
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
		return map.getOrDefault(name.toLowerCase(Locale.ROOT), fallback);
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
