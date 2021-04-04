package de.siphalor.tweed;

import de.siphalor.tweed.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed.data.serializer.ConfigDataSerializer;

public interface TweedInitializer {
	/**
	 * Register {@link de.siphalor.tweed.tailor.Tailor}s or {@link de.siphalor.tweed.data.serializer.ConfigDataSerializer}s in this method.
	 * @see de.siphalor.tweed.config.TweedRegistry#TAILORS
	 * @see de.siphalor.tweed.config.TweedRegistry#SERIALIZERS
	 * @see de.siphalor.tweed.config.POJOConverter#registerSerializer(Class, ConfigValueSerializer)
	 */
	default void register() {

	}

	/**
	 * Register {@link de.siphalor.tweed.config.ConfigFile}s in this method.
	 * @see de.siphalor.tweed.config.TweedRegistry#registerConfigFile(String, ConfigDataSerializer)
	 * @see de.siphalor.tweed.config.TweedRegistry#registerConfigPOJO(Object, String)
	 */
	default void init() {

	}
}
