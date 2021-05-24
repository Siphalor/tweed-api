package de.siphalor.tweed4;

import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.data.serializer.ConfigDataSerializer;

public interface TweedInitializer {
	/**
	 * Register {@link de.siphalor.tweed4.tailor.Tailor}s or {@link de.siphalor.tweed4.data.serializer.ConfigDataSerializer}s in this method.
	 * @see de.siphalor.tweed4.config.TweedRegistry#TAILORS
	 * @see de.siphalor.tweed4.config.TweedRegistry#SERIALIZERS
	 */
	default void tweedRegister() {

	}

	/**
	 * Register {@link de.siphalor.tweed4.config.ConfigFile}s in this method.
	 * @see de.siphalor.tweed4.config.TweedRegistry#registerConfigFile(String, ConfigDataSerializer)
	 * @see de.siphalor.tweed4.config.TweedRegistry#registerConfigFile(ConfigFile)
	 */
	default void tweedInit() {

	}
}
