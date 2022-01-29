package de.siphalor.tweed4.annotated;

import de.siphalor.tweed4.config.entry.ConfigEntry;
import de.siphalor.tweed4.config.value.serializer.ConfigSerializers;

import java.lang.reflect.Field;

public interface POJOConfigEntryMapper {
	ConfigEntry<?> map(Field field, Object value, ConfigSerializers.SerializerResolver resolver);
}
