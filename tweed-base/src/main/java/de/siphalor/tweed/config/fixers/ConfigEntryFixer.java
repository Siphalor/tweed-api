package de.siphalor.tweed.config.fixers;

import de.siphalor.tweed.data.DataObject;

/**
 * Represents a data fixer for specific config entries
 */
public abstract class ConfigEntryFixer {
	/**
	 * This method should fixed the specified config entry
	 * @param dataObject The read in data object where this entry is located.
	 * @param propertyName The id of the entry to be fixed
	 * @param mainCompound The main object which was read in. This object should be changed to represent the new data structure
	 */
	public abstract void fix(DataObject<?> dataObject, String propertyName, DataObject<?> mainCompound);
}
