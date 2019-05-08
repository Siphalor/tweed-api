package de.siphalor.tweed.config.fixers;

import de.siphalor.tweed.Core;
import org.apache.commons.lang3.StringUtils;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

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
	public void fix(JsonObject jsonObject, String propertyName, JsonObject mainObject) {
		JsonValue jsonValue = jsonObject.get(propertyName);
		if(jsonValue == null) return;
		jsonObject.remove(propertyName);

		if(newLocation == null) {
			jsonObject.add(newName, jsonValue);
		} else {
			JsonObject location = mainObject;
			String[] parts = StringUtils.split(newLocation, Core.HJSON_PATH_DELIMITER);
			for(String part : parts) {
				if(location.get(part) == null) {
					location.add(part, new JsonObject());
					location = location.get(part).asObject();
				} else {
					if(location.get(part).isObject()) {
						location = location.get(part).asObject();
					} else {
						System.err.println("Unable to fix Tweed config file");
						return;
					}
				}
			}
            location.set(newName, jsonValue);
		}
	}
}
