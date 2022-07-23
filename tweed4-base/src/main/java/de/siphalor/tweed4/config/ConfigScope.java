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

import org.jetbrains.annotations.ApiStatus;

/**
 * An enum which sets when a config can be (re-)loaded. <br><br>
 *
 * HIGHEST: guaranteed to be the highest scope (internal usage) <br>
 * GAME: triggered when game starts <br>
 * WORLD: triggered when joining a world <br>
 * SMALLEST: always reload if some kind of reload is pushed <br>
 * DEFAULT: <i>should only be used internally</i>
 */
public enum ConfigScope {
	/**
	 * Guaranteed to be the highest scope.
	 * Triggers all other scopes.
	 * Intended for internal usage.
	 */
	HIGHEST(3),
	/**
	 * Triggered when the game is started.
	 */
	GAME(2),
	/**
	 * Triggered when joining a world
	 */
	WORLD(1),
	/**
	 * The smallest scope, is triggered for any scope.
	 */
	SMALLEST(0),
	/**
	 * The default value for some functions.
	 * This usually indicates that the scope should be inherited.
	 * Intended for internal use only.
	 */
	@ApiStatus.Internal
	DEFAULT(-1);

	private final int value;

	ConfigScope(int value) {
		this.value = value;
	}

	/**
	 * Gets whether this scope triggers the other one.
	 * Since higher scopes always trigger smaller scopes, this effectively returns whether this scope is greater than the other.
	 * @param other Another scope to check against
	 * @return Whether this scope triggers the other one
	 */
	public boolean triggers(ConfigScope other) {
		return this.value >= other.value;
	}
}
