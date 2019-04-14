package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.config.constraints.ConstraintException;
import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

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
	void read(JsonValue json, ConfigEnvironment environment, ConfigScope scope) throws ConfigReadException;

	/**
	 * Read this kind of entry from a packet.
	 * @param buf the packet's buffer
	 */
	void read(PacketByteBuf buf);

	/**
	 * Write this kind of entry to a packet.
	 * @param buf the packet's buffer
	 */
	void write(PacketByteBuf buf);

	/**
	 * Method to writeValue the entry to HJSON (to be readValue by the user).
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
