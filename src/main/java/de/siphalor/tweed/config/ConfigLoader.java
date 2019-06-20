package de.siphalor.tweed.config;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.data.DataObject;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;

/**
 * Used to reload the {@link ConfigFile}s.
 */
public final class ConfigLoader {
	public static void initialReload(ConfigEnvironment configEnvironment) {
		TweedRegistry.getConfigFiles().forEach(configFile -> {
			configFile.load(readMainConfigFile(configFile), configEnvironment, ConfigScope.HIGHEST, ConfigOrigin.MAIN);
			updateMainConfigFile(configFile, configEnvironment, ConfigScope.HIGHEST);
			configFile.finishReload(configEnvironment, ConfigScope.HIGHEST);
		});
	}

	/**
	 * Reloads all matching {@link ConfigFile}s.
	 * @param resourceManager the current {@link ResourceManager}
	 * @param environment the current environment
	 * @param scope the definition scope
	 */
	public static void loadConfigs(ResourceManager resourceManager, ConfigEnvironment environment, ConfigScope scope) {
		Collection<ConfigFile> configFiles = TweedRegistry.getConfigFiles();
		for(ConfigFile configFile : configFiles) {
			configFile.reset(environment, scope);
			configFile.load(readMainConfigFile(configFile), environment, scope, ConfigOrigin.MAIN);
            updateMainConfigFile(configFile, environment, scope);
			try {
				List<Resource> resources = resourceManager.getAllResources(configFile.getFileIdentifier());
				for(Resource resource : resources) {
					configFile.load(resource, environment, scope, ConfigOrigin.DATAPACK);
				}
			} catch (Exception ignored) {}
			configFile.finishReload(environment, scope);
			if(ConfigEnvironment.SERVER.contains(environment)) {
				configFile.syncToClients(ConfigEnvironment.SYNCED, scope, ConfigOrigin.DATAPACK);
			}
		}
	}

	/**
	 * Updates the main file of a {@link ConfigFile}
	 * @param configFile the config file
	 * @param environment the current environment
	 * @param scope the definition scope
	 */
	public static void updateMainConfigFile(ConfigFile configFile, ConfigEnvironment environment, ConfigScope scope) {
        DataObject dataObject = readMainConfigFile(configFile);
        configFile.write(dataObject, environment, scope);
		File mainConfigFile = getMainConfigFile(configFile);
		//noinspection ResultOfMethodCallIgnored
		mainConfigFile.toPath().getParent().toFile().mkdirs();
		try {
			FileOutputStream outputStream = new FileOutputStream(mainConfigFile);
			configFile.getDataSerializer().write(outputStream, dataObject);
            outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			Core.LOGGER.error("Failed to update config file " + configFile.getFileName());
		}
	}

	/**
	 * Reads data from the main config file
	 * @param configFile the config file
	 * @return the read in data
	 */
	public static DataObject readMainConfigFile(ConfigFile configFile) {
		File mainConfig = getMainConfigFile(configFile);
		if(mainConfig.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(mainConfig);
				DataObject dataObject = configFile.getDataSerializer().read(inputStream);
                inputStream.close();
                if(dataObject == null) {
                	Core.LOGGER.error("Failed to read config file " + configFile.getFileName());
					return configFile.getDataSerializer().newCompound();
				}
				return dataObject;
			} catch (Exception ignored) {
				Core.LOGGER.error("Failed to read config file " + configFile.getFileName());
			}
		}
		return configFile.getDataSerializer().newCompound();
	}

	/**
	 * Writes the current data to the main config file
	 * @param configFile the config file
	 * @param environment the current environment
	 * @param scope the definition scope
	 */
	public static void writeMainConfigFile(ConfigFile configFile, ConfigEnvironment environment, ConfigScope scope) {
		File mainConfigFile = getMainConfigFile(configFile);
		//noinspection ResultOfMethodCallIgnored
		mainConfigFile.toPath().getParent().toFile().mkdirs();
		try {
			FileOutputStream outputStream = new FileOutputStream(mainConfigFile);
			configFile.getDataSerializer().write(outputStream, configFile.write(configFile.getDataSerializer().newCompound(), environment, scope));
            outputStream.close();
		} catch (Exception e) {
			Core.LOGGER.error("Failed to load config file " + configFile.getFileName());
			e.printStackTrace();
		}
	}

	/**
	 * Gets the main file for a {@link ConfigFile}
	 * @param configFile the config file
	 * @return a {@link File} for the main config file
	 */
	public static File getMainConfigFile(ConfigFile configFile) {
		return new File(Core.mainConfigDirectory, configFile.getFileName());
	}
}
