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

package de.siphalor.tweed4.config;

/**
 * An enum which defines in which environment a config should be loaded.
 *
 * CLIENT: only clientside
 * SERVER: only serverside
 * SYNCED: configured at server side but synchronized to the clients
 * UNIVERSAL: on both sides
 * DEFAULT: only to be used internally
 */
public enum ConfigEnvironment {
	UNIVERSAL(null), CLIENT(UNIVERSAL), SERVER(UNIVERSAL), SYNCED(SERVER), DEFAULT(null);

	public final ConfigEnvironment parent;

	ConfigEnvironment(ConfigEnvironment parent) {
		this.parent = parent;
	}

	public boolean contains(ConfigEnvironment other) {
        while(other != null) {
        	if(this == other)
        		return true;
        	other = other.parent;
		}
        return false;
	}

	/**
	 * Checks if this environment triggers updates in the other one.
	 * @param other Another environment.
	 * @return Whether an update would be propagated.
	 */
	public boolean triggers(ConfigEnvironment other) {
		if (other == UNIVERSAL)
			return true;
		return contains(other);
	}
}
