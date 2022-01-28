package de.siphalor.tweed4.annotated;

import de.siphalor.tweed4.config.value.serializer.ConfigValueSerializer;

import java.lang.reflect.Type;

public interface POJOConfigValueSerializerFactory<V, Serializer extends ConfigValueSerializer<V>> {
	Serializer create(V value, Class<V> clazz, Type type);
}
