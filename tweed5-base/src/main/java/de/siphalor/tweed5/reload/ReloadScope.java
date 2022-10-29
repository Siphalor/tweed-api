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

package de.siphalor.tweed5.reload;

import de.siphalor.tweed5.util.EnumRepresentation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;

/**
 * An enum which sets when a config can be (re-)loaded.
 */
public class ReloadScope implements EnumRepresentation.EnumLike {
	/**
	 * Indicates that no scope is set, the actual scope will be determined by the implementation (e.g. via parents or children)
	 */
	@ApiStatus.Internal
	public static final ReloadScope UNSPECIFIED = new ReloadScope("unspecified", Integer.MAX_VALUE);
	/**
	 * The highest scope, triggers all other scopes
	 */
	public static final ReloadScope HIGHEST = new ReloadScope("highest", Integer.MAX_VALUE - 1);
	/**
	 * Triggered when game starts
	 */
	public static final ReloadScope GAME = new ReloadScope("game", 2000);
	/**
	 * Triggered when joining a world
	 */
	public static final ReloadScope WORLD = new ReloadScope("world", 1000);
	/**
	 * Triggers on all reloads
	 */
	public static final ReloadScope SMALLEST = new ReloadScope("smallest", 0);

	public static final int SPECIAL_SCOPE_VALUE = Integer.MAX_VALUE - 1000;

	/**
	 * Enum-like representation of this class.
	 */
	public static final EnumRepresentation<ReloadScope> ENUM = EnumRepresentation.fromConstants(ReloadScope.class, Comparator.comparingInt(scope -> -scope.value));
	private final String name;
	private final int value;

	ReloadScope(String name, int value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets whether this scope triggers the other one.
	 * Since higher scopes always trigger smaller scopes, this effectively returns whether this scope is greater than the other.
	 * @param other Another scope to check against
	 * @return Whether this scope triggers the other one
	 */
	public boolean triggers(ReloadScope other) {
		if (this == UNSPECIFIED) {
			return false;
		}
		if (value == SPECIAL_SCOPE_VALUE) {
			return this == other;
		}
		return value >= other.value;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
