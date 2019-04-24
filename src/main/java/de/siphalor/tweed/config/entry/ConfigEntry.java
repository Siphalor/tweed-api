package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigOrigin;
import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.config.constraints.ConstraintException;
import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.Optional;

public interface ConfigEntry {

	/**
	 * Resets this entry to its default
	 * @param environment the current environment the current environment (handled by the system, can be ignored in most cases)
	 * @param scope the current scope the current scope (handled by the system, can be ignored in most cases)
	 */
	void reset(ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Abstract method for reading the entry's value from a {@link JsonValue} object
	 * @param json the given json value
	 * @param environment the current environment
	 * @param scope the current reload scope
	 * @throws ConfigReadException if an issue occurs during reading the value
	 */
	void read(JsonValue json, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException;

	/**
	 * Read this kind of entry from a packet.
	 * @param buf the packet's buffer
	 */
	void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Write this kind of entry to a packet.
	 * @param buf the packet's buffer
	 */
	void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Method to write the main config value of the entry to HJSON (to be read by the user).
	 * @param jsonObject the object where this entry should be appended to
	 * @param key the key under which this entry should be appended
	 * @param environment the current environment (handled by the system, can be ignored in most cases)
	 * @param scope the current scope (handled by the system, can be ignored in most cases)
	 */
	void write(JsonObject jsonObject, String key, ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Gets the environment where this entry can be defined in.
	 * @return the environment
	 */
	ConfigEnvironment getEnvironment();

	/**
	 * Gets the scope in which the entry gets reloaded.
	 * @return the scope
	 */
	ConfigScope getScope();

	/**
	 * Gives a description for what this config entry triggeredBy, possible constraints etc.
	 * @return the description
	 */
	String getDescription();

	default Optional<String[]> getClothyDescription() {
		return Optional.of(getDescription().split(System.lineSeparator()));
	}

	default String getCleanedDescription() {
		return getDescription().replace(System.lineSeparator(), "\n").replace("\t", "   ");
	}

	/**
	 * Method for handling possible constraints before reading in the value.
	 * @param jsonValue the given value
	 * @throws ConstraintException an exception
	 */
	default void applyPreConstraints(JsonValue jsonValue) throws ConstraintException {}

	/**
	 * Method for handling possible constraints after reading in the value.
	 * @param jsonValue the give value
	 * @throws ConstraintException an exception
	 */
	default void applyPostConstraints(JsonValue jsonValue) throws ConstraintException {}
}
