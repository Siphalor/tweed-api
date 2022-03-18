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

import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed4.tailor.Tailor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to register {@link ConfigFile}s.
 */
@SuppressWarnings("deprecation")
public class TweedRegistry {
	private static final ArrayList<ConfigFile> CONFIG_FILES = new ArrayList<>();
	private static ConfigDataSerializer<?, ?, ?> defaultSerializer;
	private static int serializerByExtensionSerializersHash;
	private static Map<String, DataSerializer<?, ?, ?>> serializersByExtension;

	/**
	 * This registry contains all of the known {@link ConfigDataSerializer}s.<br />
	 * By default available serializers are <code>gson</code>, <code>hjson</code> and <code>jankson</code>.
	 */
	public static final Registry<ConfigDataSerializer<?, ?, ?>> SERIALIZERS = new SimpleRegistry<>();
	/**
	 * This registry contains all of the known {@link Tailor}s.<br />
	 * By default only a serializer for the Cloth config UI is available as <code>tweed4:cloth</code>.
	 */
	public static final Registry<Tailor> TAILORS = new SimpleRegistry<>();

	public static ConfigFile registerConfigFile(String fileName) {
		return registerConfigFile(fileName, defaultSerializer);
	}

	/**
	 * Registers a new {@link ConfigFile}.
	 * @param fileName the file id which is used (no extension; no subdirectories for now)
	 * @param dataSerializer a serializer for this config file
	 * @return the new {@link ConfigFile}
	 */
	public static ConfigFile registerConfigFile(String fileName, ConfigDataSerializer<?, ?, ?> dataSerializer) {
        ConfigFile configFile = new ConfigFile(fileName, dataSerializer);
        CONFIG_FILES.add(configFile);
        return configFile;
	}

	/**
	 * Register a {@link ConfigFile}.
	 * @param file The file to register
	 * @return The registered file
	 */
	public static ConfigFile registerConfigFile(ConfigFile file) {
		CONFIG_FILES.add(file);
		return file;
	}

	/**
	 * Gets a collection of all registered {@link ConfigFile}s.
	 * @return a collection of {@link ConfigFile}s
	 * @see #registerConfigFile(String, ConfigDataSerializer)
	 */
	public static ArrayList<ConfigFile> getConfigFiles() {
		return CONFIG_FILES;
	}

	public static Map<String, DataSerializer<?, ?, ?>> getSerializersByExtension() {
		Set<ConfigDataSerializer<?, ?, ?>> entries = SERIALIZERS.stream().collect(Collectors.toSet());
		Set<Identifier> ids = SERIALIZERS.getIds();
		int entriesHash = entries.hashCode() ^ ids.hashCode();
		if (serializersByExtension != null && serializerByExtensionSerializersHash == entriesHash) {
			return serializersByExtension;
		}

		serializersByExtension = new HashMap<>();
		for (Identifier id : ids) {
			ConfigDataSerializer<?, ?, ?> serializer = SERIALIZERS.get(id);
			if (serializer instanceof DataSerializer) {
				serializersByExtension.put(serializer.getFileExtension(), ((DataSerializer<?, ?, ?>) serializer));
			}
		}
		serializerByExtensionSerializersHash = entriesHash;
		return serializersByExtension;
	}

	/**
	 * Gets the fallback config serializer.
	 * @return The default config serializer
	 */
	public static ConfigDataSerializer<?, ?, ?> getDefaultSerializer() {
		return defaultSerializer;
	}

	/**
	 * Sets the default serializer to use when none is set.
	 * @param defaultSerializer The new default serializer
	 * @deprecated Only for internal use!
	 */
	@Deprecated
	@ApiStatus.Internal
	public static void setDefaultSerializer(ConfigDataSerializer<?, ?, ?> defaultSerializer) {
		TweedRegistry.defaultSerializer = defaultSerializer;
	}
}
