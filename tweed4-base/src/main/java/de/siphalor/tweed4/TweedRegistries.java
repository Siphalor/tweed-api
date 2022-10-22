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

package de.siphalor.tweed4;

import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.registry.AutoRegistry;
import de.siphalor.tweed4.registry.Registry;
import de.siphalor.tweed4.registry.SerializerRegistry;
import de.siphalor.tweed4.registry.TweedIdentifier;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.tailor.Tailor;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used to register {@link ConfigFile}s.
 */
public class TweedRegistries {
	public static final AutoRegistry<String, ConfigFile> CONFIG_FILES = new AutoRegistry<>(ConfigFile::getName);
	public static final SerializerRegistry SERIALIZERS = new SerializerRegistry();
	public static final Registry<TweedIdentifier, Tailor> TAILORS = new Registry<>();
	private static DataSerializer<?> defaultSerializer;

	/**
	 * Gets the fallback config serializer.
	 * @return The default config serializer
	 */
	public static DataSerializer<?> getDefaultSerializer() {
		return defaultSerializer;
	}

	/**
	 * Sets the default serializer to use when none is set.
	 * @param defaultSerializer The new default serializer
	 * @deprecated Only for internal use!
	 */
	@Deprecated
	@ApiStatus.Internal
	public static void setDefaultSerializer(DataSerializer<?> defaultSerializer) {
		TweedRegistries.defaultSerializer = defaultSerializer;
	}
}
