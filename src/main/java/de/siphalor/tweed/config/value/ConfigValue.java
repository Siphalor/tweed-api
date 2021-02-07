package de.siphalor.tweed.config.value;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.value.serializer.*;
import de.siphalor.tweed.util.StaticStringConvertible;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public static <V> StringMapSerializer<V, HashMap<String, V>> stringMapSerializer(ConfigValueSerializer<V> valueSerializer) {
		return new StringMapSerializer<>(valueSerializer, HashMap::new);
	}

	public static <V, M extends Map<String, V>> StringMapSerializer<V, M> stringMapSerializer(ConfigValueSerializer<V> valueSerializer, Supplier<M> mapSupplier) {
		return new StringMapSerializer<>(valueSerializer, mapSupplier);
	}

	public static ConfigValueSerializer<?> serializer(Object value) {
		return serializer(value, value.getClass());
	}

	public static ConfigValueSerializer<?> serializer(Object value, Class<?> clazz) {
		ConfigValueSerializer<?> serializer = serializerByClass(clazz);
		if (serializer == null) {
			return specialSerializer(value);
		}
		return serializer;
	}

	public static ConfigValueSerializer<?> serializer(Object value, Type type) {
		ConfigValueSerializer<?> serializer;
		Class<?> clazz = null;
		if (type instanceof ParameterizedType) {
			serializer = containerSerializer((ParameterizedType) type);
			if (serializer != null) {
				return serializer;
			}
			if (((ParameterizedType) type).getRawType() instanceof Class<?>) {
				clazz = (Class<?>) ((ParameterizedType) type).getRawType();
			}
		} else if (type instanceof Class<?>) {
			clazz = (Class<?>) type;
		}

		if (clazz != null) {
			return serializer(value, clazz);
		}
		return null;
	}

	public static ConfigValueSerializer<?> containerSerializer(ParameterizedType type) {
		Type rawType = type.getRawType();
		if (rawType instanceof Class<?>) {
			if (List.class.isAssignableFrom((Class<?>) rawType)) {
				try {
					Constructor<?> constructor = ((Class<?>) rawType).getConstructor();
					constructor.setAccessible(true);
					//noinspection unchecked
					return listSerializer((ConfigValueSerializer<Object>) serializer(null, type.getActualTypeArguments()[0]), () -> {
						try {
							//noinspection unchecked
							return (List<Object>) constructor.newInstance();
						} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
							Tweed.LOGGER.error("Failed to create new list for config entry!");
							e.printStackTrace();
						}
						return new ArrayList<>();
					});
				} catch (NoSuchMethodException e) {
					if (((Class<?>) rawType).isAssignableFrom(ArrayList.class)) {
						return listSerializer(serializer(null, type.getActualTypeArguments()[0]), ArrayList::new);
					}
				}
			} else if (Map.class.isAssignableFrom((Class<?>) rawType)) {
				if (type.getActualTypeArguments()[0] == String.class) {
					try {
						Constructor<?> constructor = ((Class<?>) rawType).getConstructor();
						constructor.setAccessible(true);
						//noinspection unchecked
						return stringMapSerializer((ConfigValueSerializer<Object>) serializer(null, type.getActualTypeArguments()[1]), () -> {
							try {
								//noinspection unchecked
								return (Map<String, Object>) constructor.newInstance();
							} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
								Tweed.LOGGER.error("Failed to create new map for config entry!");
								e.printStackTrace();
							}
							return new HashMap<>();
						});
					} catch (NoSuchMethodException e) {
						if (((Class<?>) rawType).isAssignableFrom(HashMap.class)) {
							return stringMapSerializer(serializerByClass((Class<?>) type.getActualTypeArguments()[1]), HashMap::new);
						}
					}
				}
			}
		}
		return null;
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
