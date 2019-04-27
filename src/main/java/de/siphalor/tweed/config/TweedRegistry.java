package de.siphalor.tweed.config;

import java.util.ArrayList;

/**
 * Used to register {@link ConfigFile}s.
 */
public class TweedRegistry {
	private static ArrayList<ConfigFile> configFiles = new ArrayList<>();

	/**
	 * Registers a new {@link ConfigFile}.
	 * Trigger {@link ConfigFile#triggerInitialLoad()} after registering all your entries in your ModInitializer.
	 * @param fileName the file name which is used (no extension; no subdirectories for now)
	 * @return the new {@link ConfigFile}
	 */
	public static ConfigFile registerConfigFile(String fileName) {
        ConfigFile configFile = new ConfigFile(fileName);
        configFiles.add(configFile);
        return configFile;
	}

	/**
	 * Gets a collection of all registered {@link ConfigFile}s.
	 * @return a collection of {@link ConfigFile}s
	 * @see #registerConfigFile(String)
	 */
	public static ArrayList<ConfigFile> getConfigFiles() {
		return configFiles;
	}
}
