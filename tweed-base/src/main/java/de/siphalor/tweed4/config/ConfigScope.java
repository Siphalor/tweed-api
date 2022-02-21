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
 * An enum which sets when a config can be (re-)loaded. <br><br>
 *
 * HIGHEST: guaranteed to be the highest scope (internal usage) <br>
 * GAME: reload when game starts <br>
 * WORLD: unused <br>
 * SMALLEST: always reload if some kind of reload is pushed <br>
 * DEFAULT: <i>should only be used internally</i>
 */
public enum ConfigScope {
	HIGHEST(3),
	GAME(2),
	WORLD(1),
	SMALLEST(0),
	DEFAULT(-1);

	private final int value;

	ConfigScope(int value) {
		this.value = value;
	}

	@Deprecated
	public boolean triggeredBy(ConfigScope other) {
		return this.value <= other.value;
	}

	public boolean triggers(ConfigScope other) {
		return this.value >= other.value;
	}
}
