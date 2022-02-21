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

import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;

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
	public abstract <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void fix(O dataObject, String propertyName, O mainCompound);
}
