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

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed4.tailor.Tailor;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Used to register {@link ConfigFile}s.
 */
@SuppressWarnings("deprecation")
public class TweedRegistry {
	private static final Map<String, ConfigFile> CONFIG_FILES = new HashMap<>();
	private static ConfigDataSerializer<?, ?, ?> defaultSerializer;
	private static int serializerByExtensionSerializersHash;
	private static Map<String, DataSerializer<?, ?, ?>> serializersByExtension;

	/**
	 * This registry contains all the known {@link ConfigDataSerializer}s.<br />
	 */
	@SuppressWarnings("rawtypes")
	public static final Registry<ConfigDataSerializer> SERIALIZERS = FabricRegistryBuilder.createSimple(
			ConfigDataSerializer.class, new Identifier(Tweed.MOD_ID, "serializers")
	).buildAndRegister();
	/**
	 * This registry contains all the known {@link Tailor}s.<br />
	 */
	public static final Registry<Tailor> TAILORS = FabricRegistryBuilder.createSimple(
			Tailor.class, new Identifier(Tweed.MOD_ID, "tailors")
	).buildAndRegister();

	/**
	 * Registers a new {@link ConfigFile}.
	 * The default serializer will be used (usually the HJSON serializer, if around).
	 * @param fileName the file id which is used (without extension)
	 * @return the new {@link ConfigFile}
	 * @deprecated it is highly recommended to explicitly set the serializer with {@link #registerConfigFile(String, ConfigDataSerializer)}.
	 */
	@Deprecated
	public static ConfigFile registerConfigFile(String fileName) {
		return registerConfigFile(fileName, defaultSerializer);
	}

	/**
	 * Registers a new {@link ConfigFile}.
	 * @param fileName the file id which is used (without extension)
	 * @param dataSerializer a serializer for this config file
	 * @return the new {@link ConfigFile}
	 */
	public static ConfigFile registerConfigFile(String fileName, ConfigDataSerializer<?, ?, ?> dataSerializer) {
        ConfigFile configFile = new ConfigFile(fileName, dataSerializer);
		return registerConfigFile(configFile);
	}

	/**
	 * Register a {@link ConfigFile}.
	 * @param file The file to register
	 * @return The registered file
	 */
	public static ConfigFile registerConfigFile(ConfigFile file) {
		String name = file.getName();
		if (CONFIG_FILES.containsKey(name)) {
			Tweed.LOGGER.error("Config file with id '" + name + "' already registered!");
			return null;
		}
		CONFIG_FILES.put(name, file);
		return file;
	}

	/**
	 * Gets a collection of all registered {@link ConfigFile}s.
	 * @return a collection of {@link ConfigFile}s
	 * @see #registerConfigFile(String, ConfigDataSerializer)
	 * @deprecated use {@link #getAllConfigFiles()} instead
	 */
	@Deprecated
	public static ArrayList<ConfigFile> getConfigFiles() {
		return new ArrayList<>(CONFIG_FILES.values());
	}

	/**
	 * Gets a collection of all registered {@link ConfigFile}s.
	 * @return a collection of {@link ConfigFile}s
	 * @see #registerConfigFile(String, ConfigDataSerializer)
	 */
	public static Collection<ConfigFile> getAllConfigFiles() {
		return CONFIG_FILES.values();
	}

	/**
	 * Gets a {@link ConfigFile} by its name.
	 * @param name the name of the file
	 * @return the {@link ConfigFile} or <code>null</code> if not found
	 */
	public static ConfigFile getConfigFile(String name) {
		return CONFIG_FILES.get(name);
	}

	/**
	 * Gets a map of all {@link DataSerializer}s by their file extension.
	 * This is cached and will be updated if a new {@link DataSerializer} is registered.
	 * @return a map of {@link DataSerializer}s by their file extension
	 */
	public static Map<String, DataSerializer<?, ?, ?>> getSerializersByExtension() {
		//noinspection rawtypes
		Set<Map.Entry<RegistryKey<ConfigDataSerializer>, ConfigDataSerializer>> entries = SERIALIZERS.getEntrySet();
		int entriesHash = entries.hashCode();
		if (serializersByExtension != null && serializerByExtensionSerializersHash == entriesHash) {
			return serializersByExtension;
		}

		serializersByExtension = new HashMap<>();
		//noinspection rawtypes
		for (Map.Entry<RegistryKey<ConfigDataSerializer>, ConfigDataSerializer> entry : entries) {
			if (entry.getValue() instanceof DataSerializer) {
				serializersByExtension.put(entry.getValue().getFileExtension(), ((DataSerializer<?, ?, ?>) entry.getValue()));
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
