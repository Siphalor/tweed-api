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

package de.siphalor.tweed4.config.value.serializer;

import com.mojang.datafixers.util.Either;
import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.util.ReflectionUtil;
import de.siphalor.tweed4.util.StaticStringConvertible;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

public class ConfigSerializers {
	private static final BooleanSerializer BOOLEAN_SERIALIZER = new BooleanSerializer();
	private static final ByteSerializer BYTE_SERIALIZER = new ByteSerializer();
	private static final ShortSerializer SHORT_SERIALIZER = new ShortSerializer();
	private static final IntegerSerializer INTEGER_SERIALIZER = new IntegerSerializer();
	private static final LongSerializer LONG_SERIALIZER = new LongSerializer();
	private static final FloatSerializer FLOAT_SERIALIZER = new FloatSerializer();
	private static final DoubleSerializer DOUBLE_SERIALIZER = new DoubleSerializer();
	private static final CharacterSerializer CHARACTER_SERIALIZER = new CharacterSerializer();
	private static final StringSerializer STRING_SERIALIZER = new StringSerializer();

	public static BooleanSerializer getBoolean() {
		return BOOLEAN_SERIALIZER;
	}

	public static ByteSerializer getByte() {
		return BYTE_SERIALIZER;
	}

	public static ShortSerializer getShort() {
		return SHORT_SERIALIZER;
	}

	public static IntegerSerializer getInteger() {
		return INTEGER_SERIALIZER;
	}

	public static LongSerializer getLong() {
		return LONG_SERIALIZER;
	}

	public static FloatSerializer getFloat() {
		return FLOAT_SERIALIZER;
	}

	public static DoubleSerializer getDouble() {
		return DOUBLE_SERIALIZER;
	}

	public static CharacterSerializer getCharacter() {
		return CHARACTER_SERIALIZER;
	}

	public static StringSerializer getString() {
		return STRING_SERIALIZER;
	}

	public static <E> ListSerializer<E, ArrayList<E>> createList(ConfigValueSerializer<E> elementSerializer) {
		return new ListSerializer<>(elementSerializer, ArrayList::new);
	}

	public static <E, L extends List<E>>
	ListSerializer<E, L> createList(ConfigValueSerializer<E> elementSerializer, Supplier<L> listSupplier) {
		return new ListSerializer<>(elementSerializer, listSupplier);
	}

	public static <E extends Enum<?>> EnumSerializer<E> createEnum(E fallback) {
		return new EnumSerializer<>(fallback);
	}

	public static <E extends Enum<?>> EnumSerializer<E> createEnum(E fallback, E[] enumConstants) {
		return new EnumSerializer<>(fallback, enumConstants);
	}

	public static <T> OptionalSerializer<T> createOptional(ConfigValueSerializer<T> valueSerializer) {
		return new OptionalSerializer<>(valueSerializer);
	}

	public static <A, B> EitherSerializer<A, B> createEither(ConfigValueSerializer<A> leftSerializer, ConfigValueSerializer<B> rightSerializer) {
		return new EitherSerializer<>(leftSerializer, rightSerializer);
	}

	public static <T> NullableSerializer<T> createNullable(ConfigValueSerializer<T> valueSerializer) {
		return new NullableSerializer<>(valueSerializer);
	}

	public static <T> ConfigValueSerializer<T> deduce(T value, Class<T> clazz, Type type, SerializerResolver resolver) {
		return deduce(value, clazz, type, resolver, true);
	}

	@SuppressWarnings("unchecked")
	public static <T> ConfigValueSerializer<T> deduce(T value, Class<T> clazz, Type type, SerializerResolver resolver, boolean reflectiveSerializer) {
		if (clazz == Boolean.class || clazz == Boolean.TYPE) {
			return (ConfigValueSerializer<T>) getBoolean();
		}
		if (clazz == Byte.class || clazz == Byte.TYPE) {
			return (ConfigValueSerializer<T>) getByte();
		}
		if (clazz == Short.class || clazz == Short.TYPE) {
			return (ConfigValueSerializer<T>) getShort();
		}
		if (clazz == Integer.class || clazz == Integer.TYPE) {
			return (ConfigValueSerializer<T>) getInteger();
		}
		if (clazz == Long.class || clazz == Long.TYPE) {
			return (ConfigValueSerializer<T>) getLong();
		}
		if (clazz == Float.class || clazz == Float.TYPE) {
			return (ConfigValueSerializer<T>) getFloat();
		}
		if (clazz == Double.class || clazz == Double.TYPE) {
			return (ConfigValueSerializer<T>) getDouble();
		}
		if (clazz == Character.class || clazz == Character.TYPE) {
			return (ConfigValueSerializer<T>) getCharacter();
		}
		if (clazz == String.class) {
			return (ConfigValueSerializer<T>) getString();
		}
		if (Enum.class.isAssignableFrom(clazz)) {
			if (value != null) {
				return (ConfigValueSerializer<T>) createEnum((Enum<?>) value);
			}

			//noinspection rawtypes
			return (ConfigValueSerializer<T>) createEnum((Enum) clazz.getEnumConstants()[0], (Enum[]) clazz.getEnumConstants());
		}
		if (StaticStringConvertible.class.isAssignableFrom(clazz)) {
			//noinspection rawtypes
			return new StringConvertibleSerializer((StaticStringConvertible<?>) value);
		}
		if (clazz == Optional.class) {
			Object subValue;
			if (value == null) {
				subValue = null;
			} else {
				subValue = ((Optional<?>) value).orElse(null);
			}
			if (type instanceof ParameterizedType) {
				Type subType = ((ParameterizedType) type).getActualTypeArguments()[0];
				ConfigValueSerializer<Object> subSerializer = resolver.resolve(subValue, ((Class<Object>) subType), subType);
				return ((ConfigValueSerializer<T>) new OptionalSerializer<>(subSerializer));
			}
		}
		if (clazz == Either.class) {
			if (type instanceof ParameterizedType) {
				Type[] subTypes = ((ParameterizedType) type).getActualTypeArguments();
				return ((ConfigValueSerializer<T>) new EitherSerializer<>(
						resolver.resolve(null, ((Class<Object>) subTypes[0]), subTypes[0]),
						resolver.resolve(null, ((Class<Object>) subTypes[1]), subTypes[1])
				));
			}
		}
		if (List.class.isAssignableFrom(clazz)) {
			if (type instanceof ParameterizedType) {
				if (clazz.isInterface()) {
					clazz = (Class<T>) value.getClass();
				}

				Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
				if (typeArguments.length == 1) {
					List<?> list = ((List<?>) value);
					Supplier<List<Object>> listSupplier;

					try {
						Constructor<T> constructor = clazz.getDeclaredConstructor();
						listSupplier = () -> {
							try {
								return (List<Object>) constructor.newInstance();
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
								Tweed.LOGGER.warn(
										"Couldn't construct new "
												+ constructor.getDeclaringClass().getSimpleName()
												+ ". Defaulting to ArrayList."
								);
								e.printStackTrace();
							}
							return new ArrayList<>();
						};
					} catch (NoSuchMethodException e) {
						Tweed.LOGGER.warn(
								"Couldn't get constructor for " + clazz.getSimpleName() + ". Defaulting to ArrayList."
						);
						listSupplier = ArrayList::new;
					}

					return (ConfigValueSerializer<T>) createList(
							resolver.resolve(
									list.isEmpty() ? null : list.get(0),
									((Class<Object>) typeArguments[0]),
									typeArguments[0]
							),
							listSupplier
					);
				}
			}
		}

		if (!reflectiveSerializer) {
			return null;
		}

		// Reflective object serializer
		Field[] fields = ReflectionUtil.getAllDeclaredFields(clazz);
		Map<String, ReflectiveObjectSerializer.Entry> reflectiveEntries = new HashMap<>();
		for (Field field : fields) {
			int modifiers = field.getModifiers();
			if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)) {
				continue;
			}
			if (Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers)) {
				continue;
			}

			try {
				ConfigValueSerializer<Object> fieldSerializer = resolver.resolve(
						value != null ? field.get(value) : null,
						((Class<Object>) field.getType()),
						field.getGenericType()
				);
				if (field.isAnnotationPresent(ReflectiveNullable.class)) {
					fieldSerializer = new NullableSerializer<>(fieldSerializer);
				}

				reflectiveEntries.put(field.getName(),
						new ReflectiveObjectSerializer.Entry(field, fieldSerializer)
				);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (!reflectiveEntries.isEmpty()) {
			Class<T> finalClazz = clazz;
			return new ReflectiveObjectSerializer<>(
					clazz,
					() -> {
						try {
							return finalClazz.getConstructor().newInstance();
						} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
							Tweed.LOGGER.error(
									"Failed to instantiate class " + finalClazz.getSimpleName() + " in list serializer!"
							);
							e.printStackTrace();
						}
						return null;
					},
					reflectiveEntries
			);
		}

		return null;
	}

	@FunctionalInterface
	public interface SerializerResolver {
		<T> ConfigValueSerializer<T> resolve(T value, Class<T> clazz, Type type);
	}
}
