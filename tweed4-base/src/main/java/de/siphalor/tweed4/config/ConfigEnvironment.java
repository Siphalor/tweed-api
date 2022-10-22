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

import de.siphalor.tweed4.util.EnumRepresentation;
import org.jetbrains.annotations.ApiStatus;

/**
 * An enum-like class, which defines in which environment a config should be loaded.
 */
public class ConfigEnvironment implements EnumRepresentation.EnumLike {
	/**
	 * The unspecified environment. Indicates that the environment should be determined by the implementation (e.g. via parents or children).
	 */
	@ApiStatus.Internal
	public static final ConfigEnvironment UNSPECIFIED = new ConfigEnvironment("unspecified");
	/**
	 * The client environment. Indicates that the config should only be loaded on clients.
	 */
	public static final ConfigEnvironment CLIENT = new ConfigEnvironment("client");
	/**
	 * The server environment. Indicates that the config should only be loaded on dedicated servers.
	 */
	public static final ConfigEnvironment SERVER = new ConfigEnvironment("server");
	/**
	 * The common environment. Indicates that the config should be loaded on both clients and dedicated servers, but synced from the server to the client.
	 */
	public static final ConfigEnvironment SYNCED = new ConfigEnvironment("synced");
	/**
	 * The common environment. Indicates that the config should be loaded on both clients and dedicated servers, but not synced.
	 */
	public static final ConfigEnvironment UNIVERSAL = new ConfigEnvironment("universal");

	/**
	 * Enum-like representation of this class.
	 */
	public static final EnumRepresentation<ConfigEnvironment> ENUM = EnumRepresentation.fromConstants(UNSPECIFIED, ConfigEnvironment.class);

	private final String name;

	public ConfigEnvironment(String name) {
		this.name = name;
	}

	/**
	 * Checks if this environment triggers updates in the other one.
	 * @param other Another environment.
	 * @return Whether an update would be propagated.
	 */
	public boolean triggers(ConfigEnvironment other) {
		if (this == other) {
			return true;
		}
		if (UNIVERSAL == this) {
			return true;
		} else if (CLIENT == this || SERVER == this) {
			return other == UNIVERSAL || other == SYNCED;
		}

		return false;
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
