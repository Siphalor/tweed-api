package de.siphalor.tweed;

import de.siphalor.tweed.config.ConfigFile;
import de.siphalor.tweed.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed.data.serializer.ConfigDataSerializer;

public interface TweedInitializer {
	/**
	 * Register {@link de.siphalor.tweed.tailor.Tailor}s or {@link de.siphalor.tweed.data.serializer.ConfigDataSerializer}s in this method.
	 * @see de.siphalor.tweed.config.TweedRegistry#TAILORS
	 * @see de.siphalor.tweed.config.TweedRegistry#SERIALIZERS
	 */
	default void tweedRegister() {

	}

	/**
	 * Register {@link de.siphalor.tweed.config.ConfigFile}s in this method.
	 * @see de.siphalor.tweed.config.TweedRegistry#registerConfigFile(String, ConfigDataSerializer)
	 * @see de.siphalor.tweed.config.TweedRegistry#registerConfigFile(ConfigFile)
	 */
	default void tweedInit() {

	}
}
