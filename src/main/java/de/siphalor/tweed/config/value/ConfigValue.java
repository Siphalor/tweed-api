package de.siphalor.tweed.config.value;

import de.siphalor.tweed.config.value.serializer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ConfigValue<V> {
	public abstract V get();
	public abstract void set(V value);

	private static final BooleanSerializer BOOLEAN_SERIALIZER = new BooleanSerializer();
	private static final FloatSerializer FLOAT_SERIALIZER = new FloatSerializer();
	private static final IntegerSerializer INTEGER_SERIALIZER = new IntegerSerializer();
	private static final StringSerializer STRING_SERIALIZER = new StringSerializer();

	public static BooleanSerializer booleanSerializer() {
		return BOOLEAN_SERIALIZER;
	}
	public static <E extends Enum<?>> EnumSerializer<E> enumSerializer(E fallback) {
		return new EnumSerializer<>(fallback);
	}
	public static FloatSerializer floatSerializer() {
		return FLOAT_SERIALIZER;
	}
	public static IntegerSerializer integerSerializer() {
		return INTEGER_SERIALIZER;
	}
	public static <E> ListSerializer<E, ArrayList<E>> listSerializer(ConfigValueSerializer<E> elementSerializer) {
		return new ListSerializer<>(elementSerializer, ArrayList::new);
	}
	public static <E, L extends List<E>> ListSerializer<E, L> listSerializer(ConfigValueSerializer<E> elementSerializer, Supplier<L> listSupplier) {
		return new ListSerializer<>(elementSerializer, listSupplier);
	}
	public static StringSerializer stringSerializer() {
		return STRING_SERIALIZER;
	}

	public static ConfigValueSerializer<?> serializer(Object value, Class<?> clazz) {
		ConfigValueSerializer<?> serializer = serializerByClass(clazz);
		if (serializer == null) {
			return serializer(value);
		}
		return serializer;
	}

	public static ConfigValueSerializer<?> serializer(Object defaultValue) {
		if (defaultValue instanceof Enum) {
			return enumSerializer((Enum<?>) defaultValue);
		}
		return null;
	}

	public static ConfigValueSerializer<?> serializerByClass(Class<?> clazz) {
		if (clazz == Boolean.class || clazz == Boolean.TYPE) {
			return booleanSerializer();
		} else if (clazz == Float.class || clazz == Float.TYPE) {
			return floatSerializer();
		} else if (clazz == Integer.class || clazz == Integer.TYPE) {
			return integerSerializer();
		} else if (clazz == String.class) {
			return stringSerializer();
		}
		return null;
	}
}
