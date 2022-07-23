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

package de.siphalor.tweed4.config.value;

import java.lang.reflect.Field;

public class ReferenceConfigValue<V> extends ConfigValue<V> {
	Field field;
	Object object;

	public ReferenceConfigValue(Object object, Field field) {
		this.object = object;
		this.field = field;
	}

	static <V> ReferenceConfigValue<V> create(Object object, String fieldName) {
		Class<?> clazz = object.getClass();
		try {
			Field field = clazz.getDeclaredField(fieldName);

			field.setAccessible(true);

			return new ReferenceConfigValue<>(object, field);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public V get() {
		try {
			//noinspection unchecked
			return (V) field.get(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void set(V value) {
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
