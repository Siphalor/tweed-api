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

package de.siphalor.tweed5.tailor.coat;

import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.tweed5.config.entry.ValueConfigEntry;

@FunctionalInterface
public interface TweedCoatEntryProcessor<S> {
	/**
	 * Process a config value entry.
	 * @param parentWidget The parent Coat config widget that should be added to
	 * @param configEntry The config entry to process
	 * @param path The path to the config entry <i>including the name of the entry</i>
	 * @return Whether the processing should be treated as successful.
	 *         If <code>false</code> other processor will be tried for processing.
	 */
	boolean process(ConfigListWidget parentWidget, ValueConfigEntry<S> configEntry, String path);
}
