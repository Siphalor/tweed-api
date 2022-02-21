/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.config.fixers;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.data.DataList;
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
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void fix(O dataObject, String propertyName, O mainCompound) {
		V dataValue = dataObject.get(propertyName);
		if(dataValue == null) return;
		dataObject.remove(propertyName);

		if(newLocation == null) {
			dataObject.set(newName, dataValue);
		} else {
			O location = mainCompound;
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
