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

package de.siphalor.tweed4.client;

import de.siphalor.tweed4.config.ConfigFile;

/**
 * Listens for config files synchronizations from the server. <br />
 * The typical use case is to set your own listener with {@link TweedClient#setSyncListener(ConfigSyncListener)}
 * and the request a config synchronization from the server.
 */
@FunctionalInterface
public interface ConfigSyncListener {
	/**
	 * Called when a config file was synced.
	 * @param configFile The file that was synced.
	 * @return Whether this listener should be removed.
	 */
	boolean onSync(ConfigFile configFile);

	/**
	 * Called when a config file is not present on the server, so the sync failed.
	 * @param configFile The file that was not synced.
	 * @return Whether this listener should be removed.
	 */
	default boolean onFail(ConfigFile configFile) {
		return true;
	}

	/**
	 * Called when this listener is removed or replaced.
	 */
	default void onRemoved() {}
}
