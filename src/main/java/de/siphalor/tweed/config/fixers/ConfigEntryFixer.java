package de.siphalor.tweed.config.fixers;

import org.hjson.JsonObject;

/**
 * Represents a data fixer for specific config entries
 */
public abstract class ConfigEntryFixer {
	/**
	 * This method should fixed the specified config entry
	 * @param jsonObject The read in json object where this entry is located.
	 * @param propertyName The name of the entry to be fixed
	 * @param mainObject The main object which was read in. This object should be changed to represent the new data structure
	 */
	public abstract void fix(JsonObject jsonObject, String propertyName, JsonObject mainObject);
}
