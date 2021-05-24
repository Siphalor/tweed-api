package de.siphalor.tweed4.config.fixers;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import org.apache.commons.lang3.StringUtils;

/**
 * Fixes a relocated or old config file.
 */
public class ConfigEntryLocationFixer extends ConfigEntryFixer {
	private final String newName;
	private final String newLocation;

	public ConfigEntryLocationFixer(String newName) {
		this(newName, null);
	}

	public ConfigEntryLocationFixer(String newName, String newLocation) {
		this.newName = newName;
		this.newLocation = newLocation;
	}

	@Override
	public void fix(DataObject<?> dataObject, String propertyName, DataObject<?> mainCompound) {
		DataValue dataValue = dataObject.get(propertyName);
		if(dataValue == null) return;
		dataObject.remove(propertyName);

		if(newLocation == null) {
			dataObject.set(newName, dataValue);
		} else {
			DataObject location = mainCompound;
			String[] parts = StringUtils.split(newLocation, Tweed.PATH_DELIMITER);
			for(String part : parts) {
				if(location.get(part) == null) {
					location = location.addObject(part);
				} else {
					if(location.get(part).isObject()) {
						location = location.get(part).asObject();
					} else {
						Tweed.LOGGER.error("Unable to fix Tweed config file");
						return;
					}
				}
			}
            location.set(newName, dataValue);
		}
	}
}
