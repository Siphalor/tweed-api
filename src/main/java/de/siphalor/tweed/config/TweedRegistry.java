package de.siphalor.tweed.config;

import de.siphalor.tweed.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed.data.serializer.HjsonSerializer;

import java.util.ArrayList;

/**
 * Used to register {@link ConfigFile}s.
 */
public class TweedRegistry {
	private static ArrayList<ConfigFile> configFiles = new ArrayList<>();

	public static ConfigFile registerConfigFile(String fileName) {
		return registerConfigFile(fileName, HjsonSerializer.INSTANCE);
	}

	/**
	 * Registers a new {@link ConfigFile}.
	 * @param fileName the file name which is used (no extension; no subdirectories for now)
	 * @param dataSerializer a serializer for this config file
	 * @return the new {@link ConfigFile}
	 */
	public static ConfigFile registerConfigFile(String fileName, ConfigDataSerializer dataSerializer) {
        ConfigFile configFile = new ConfigFile(fileName, dataSerializer);
        configFiles.add(configFile);
        return configFile;
	}

	/**
	 * Gets a collection of all registered {@link ConfigFile}s.
	 * @return a collection of {@link ConfigFile}s
	 * @see #registerConfigFile(String, ConfigDataSerializer)
	 */
	public static ArrayList<ConfigFile> getConfigFiles() {
		return configFiles;
	}
}
