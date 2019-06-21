package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigOrigin;
import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.Optional;

/**
 * Interface for all config entries
 * @param <T> The implementing class
 */
public interface ConfigEntry<T> {

	/**
	 * Resets this entry to its default
	 * @param environment the current environment the current environment (handled by the system, can be ignored in most cases)
	 * @param scope the current scope the current scope (handled by the system, can be ignored in most cases)
	 */
	void reset(ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Abstract method for reading the entry's value from a data object
	 * @param dataValue the given data value
	 * @param environment the current environment
	 * @param scope the current reload scope
	 * @throws ConfigReadException if an issue occurs during reading the value
	 */
	void read(DataValue dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException;

	/**
	 * Read this kind of entry from a packet.
	 * @param buf the packet's buffer
	 * @param environment the current environment
	 * @param scope the current reload scope
	 * @param origin the kind of source where this data comes from/should go to
	 */
	void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin);

	/**
	 * Write this kind of entry to a packet.
	 * @param buf the packet's buffer
	 * @param environment the current environment
	 * @param scope the current reload scope
	 * @param origin the kind of source where this data comes from/should go to
	 */
	void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin);

	/**
	 * Method to write the main config value of the entry to HJSON (to be read by the user).
	 * @param dataContainer the object where this entry should be appended to
	 * @param key the key under which this entry should be appended
	 * @param environment the current environment (handled by the system, can be ignored in most cases)
	 * @param scope the current scope (handled by the system, can be ignored in most cases)
	 */
	<Key> void write(DataContainer<?, Key> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Sets the environment where this entry is defined
	 * @param environment the environment
	 * @return the current entry for chain calls
	 * @see ConfigEnvironment
	 */
	T setEnvironment(ConfigEnvironment environment);

	/**
	 * Gets the environment where this entry can be defined in.
	 * @return the environment
	 */
	ConfigEnvironment getEnvironment();

	/**
	 * Sets the scope in which the config can be (re-)loaded
	 * @param scope the scope to use
	 * @return the current entry for chain calls
	 * @see ConfigScope
	 */
	T setScope(ConfigScope scope);

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
	 * @param dataValue the given value
	 * @throws ConstraintException an exception
	 */
	default void applyPreConstraints(DataValue dataValue) throws ConstraintException {}

	/**
	 * Method for handling possible constraints after reading in the value.
	 * @param dataValue the give value
	 * @throws ConstraintException an exception
	 */
	default void applyPostConstraints(DataValue dataValue) throws ConstraintException {}
}
