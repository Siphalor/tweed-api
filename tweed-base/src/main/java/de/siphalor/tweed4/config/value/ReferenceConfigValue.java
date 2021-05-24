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
