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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ConfigValue<V> {
	public abstract V get();
	public abstract void set(V value);

	private static final BooleanSerializer BOOLEAN_SERIALIZER = new BooleanSerializer();
	private static final ByteSerializer BYTE_SERIALIZER = new ByteSerializer();
	private static final ShortSerializer SHORT_SERIALIZER = new ShortSerializer();
	private static final IntegerSerializer INTEGER_SERIALIZER = new IntegerSerializer();
	private static final LongSerializer LONG_SERIALIZER = new LongSerializer();
	private static final FloatSerializer FLOAT_SERIALIZER = new FloatSerializer();
	private static final DoubleSerializer DOUBLE_SERIALIZER = new DoubleSerializer();
	private static final CharacterSerializer CHARACTER_SERIALIZER = new CharacterSerializer();
	private static final StringSerializer STRING_SERIALIZER = new StringSerializer();

	public static BooleanSerializer booleanSerializer() {
		return BOOLEAN_SERIALIZER;
	}
	public static <E extends Enum<?>> EnumSerializer<E> enumSerializer(E fallback) {
		return new EnumSerializer<>(fallback);
	}
	public static <T extends StaticStringConvertible<T>> StringConvertibleSerializer<T> stringConvertibleSerializer(T fallback) {
		return new StringConvertibleSerializer<>(fallback);
	}
	public static ByteSerializer byteSerializer() {
		return BYTE_SERIALIZER;
	}
	public static ShortSerializer shortSerializer() {
		return SHORT_SERIALIZER;
	}
	public static IntegerSerializer integerSerializer() {
		return INTEGER_SERIALIZER;
	}
	public static LongSerializer longSerializer() {
		return LONG_SERIALIZER;
	}
	public static FloatSerializer floatSerializer() {
		return FLOAT_SERIALIZER;
	}
	public static DoubleSerializer doubleSerializer() {
		return DOUBLE_SERIALIZER;
	}
	public static CharacterSerializer characterSerializer() {
		return CHARACTER_SERIALIZER;
	}
	public static StringSerializer stringSerializer() {
		return STRING_SERIALIZER;
	}
	public static <E> ListSerializer<E, ArrayList<E>> listSerializer(ConfigValueSerializer<E> elementSerializer) {
		return new ListSerializer<>(elementSerializer, ArrayList::new);
	}
	public static <E, L extends List<E>> ListSerializer<E, L> listSerializer(ConfigValueSerializer<E> elementSerializer, Supplier<L> listSupplier) {
		return new ListSerializer<>(elementSerializer, listSupplier);
	}

	public static ConfigValueSerializer<?> serializer(Object value) {
		return serializer(value, value.getClass());
	}

	public static ConfigValueSerializer<?> serializer(Object value, Class<?> clazz, Type type) {
		ConfigValueSerializer<?> serializer;
		serializer = serializerByClass(clazz);
		if (serializer != null) {
			return serializer;
		}

		if (type instanceof ParameterizedType) {
			serializer = serializerByGeneric(value, clazz, (ParameterizedType) type);
			if (serializer != null) {
				return serializer;
			}
		}

		return value != null ? specialSerializer(value) : null;
	}

	public static ConfigValueSerializer<?> serializerByGeneric(Object value, Class<?> clazz, ParameterizedType type) {
		if (List.class.isAssignableFrom(clazz)) {
			if (clazz.isInterface()) {
				clazz = value.getClass();
			}
			Type[] typeArguments = type.getActualTypeArguments();
			if (typeArguments.length == 1) {
				List<?> list = (List<?>) value;
				Class<?> finalClazz = clazz;
				//noinspection unchecked
				return ConfigValue.listSerializer(
						((ConfigValueSerializer<Object>) serializer(
								list.isEmpty() ? null : list.get(0), (Class<?>) typeArguments[0], typeArguments[0]
						)),
						() -> {
							try {
								//noinspection unchecked
								return (List<Object>) finalClazz.getDeclaredConstructor().newInstance();
							} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
								e.printStackTrace();
							}
							return new ArrayList<>();
						}
				);
			}
		}
		return null;
	}

	public static ConfigValueSerializer<?> serializer(Object value, Class<?> clazz) {
		ConfigValueSerializer<?> serializer = serializerByClass(clazz);
		if (serializer == null) {
			return specialSerializer(value);
		}
		return serializer;
	}

	public static ConfigValueSerializer<?> specialSerializer(Object defaultValue) {
		if (defaultValue instanceof Enum) {
			return enumSerializer((Enum<?>) defaultValue);
		} else if (defaultValue instanceof StaticStringConvertible) {
			//noinspection unchecked,rawtypes
			return stringConvertibleSerializer((StaticStringConvertible) defaultValue);
		}
		return null;
	}

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
