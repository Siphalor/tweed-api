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

package de.siphalor.tweed4.config.value;

import de.siphalor.tweed4.config.value.serializer.*;
import de.siphalor.tweed4.util.StaticStringConvertible;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ConfigValue<V> {
	public abstract V get();
	public abstract void set(V value);

	@Deprecated
	public static BooleanSerializer booleanSerializer() {
		return ConfigSerializers.getBoolean();
	}
	@Deprecated
	public static <E extends Enum<?>> EnumSerializer<E> enumSerializer(E fallback) {
		return new EnumSerializer<>(fallback);
	}
	@Deprecated
	public static <T extends StaticStringConvertible<T>> StringConvertibleSerializer<T> stringConvertibleSerializer(T fallback) {
		return new StringConvertibleSerializer<>(fallback);
	}
	@Deprecated
	public static ByteSerializer byteSerializer() {
		return ConfigSerializers.getByte();
	}
	@Deprecated
	public static ShortSerializer shortSerializer() {
		return ConfigSerializers.getShort();
	}
	@Deprecated
	public static IntegerSerializer integerSerializer() {
		return ConfigSerializers.getInteger();
	}
	@Deprecated
	public static LongSerializer longSerializer() {
		return ConfigSerializers.getLong();
	}
	@Deprecated
	public static FloatSerializer floatSerializer() {
		return ConfigSerializers.getFloat();
	}
	@Deprecated
	public static DoubleSerializer doubleSerializer() {
		return ConfigSerializers.getDouble();
	}
	@Deprecated
	public static CharacterSerializer characterSerializer() {
		return ConfigSerializers.getCharacter();
	}
	@Deprecated
	public static StringSerializer stringSerializer() {
		return ConfigSerializers.getString();
	}
	@Deprecated
	public static <E> ListSerializer<E, ArrayList<E>> listSerializer(ConfigValueSerializer<E> elementSerializer) {
		return new ListSerializer<>(elementSerializer, ArrayList::new);
	}
	@Deprecated
	public static <E, L extends List<E>> ListSerializer<E, L> listSerializer(ConfigValueSerializer<E> elementSerializer, Supplier<L> listSupplier) {
		return new ListSerializer<>(elementSerializer, listSupplier);
	}

	@Deprecated
	public static ConfigValueSerializer<?> serializer(Object value) {
		return serializer(value, value.getClass());
	}

	@Deprecated
	public static ConfigValueSerializer<?> serializer(Object value, Class<?> clazz) {
		ConfigValueSerializer<?> serializer = serializerByClass(clazz);
		if (serializer == null) {
			return specialSerializer(value);
		}
		return serializer;
	}

	@Deprecated
	public static ConfigValueSerializer<?> specialSerializer(Object defaultValue) {
		if (defaultValue instanceof Enum) {
			return enumSerializer((Enum<?>) defaultValue);
		} else if (defaultValue instanceof StaticStringConvertible) {
			//noinspection unchecked,rawtypes
			return stringConvertibleSerializer((StaticStringConvertible) defaultValue);
		}
		return null;
	}

	@Deprecated
	public static ConfigValueSerializer<?> serializerByClass(Class<?> clazz) {
		if (clazz == Boolean.class || clazz == Boolean.TYPE) {
			return booleanSerializer();
		} else if (clazz == Byte.class || clazz == Byte.TYPE) {
			return byteSerializer();
		} else if (clazz == Short.class || clazz == Short.TYPE) {
			return shortSerializer();
		} else if (clazz == Integer.class || clazz == Integer.TYPE) {
			return integerSerializer();
		} else if (clazz == Long.class || clazz == Long.TYPE) {
			return longSerializer();
		} else if (clazz == Float.class || clazz == Float.TYPE) {
			return floatSerializer();
		} else if (clazz == Double.class || clazz == Double.TYPE) {
			return doubleSerializer();
		} else if (clazz == Character.class || clazz == Character.TYPE) {
			return characterSerializer();
		} else if (clazz == String.class) {
			return stringSerializer();
		}
		return null;
	}
}
