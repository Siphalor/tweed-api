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
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;

/**
 * Used to reload the {@link ConfigFile}s.
 */
public final class ConfigLoader {
	private static ThreadLocal<ResourceManager> currentResourceManager = new ThreadLocal<>();

	public static void initialReload(ConfigEnvironment configEnvironment) {
		for (ConfigFile configFile : TweedRegistry.getConfigFiles()) {
			configFile.load(readMainConfigFile(configFile).asObject(), configEnvironment, ConfigScope.HIGHEST, ConfigOrigin.MAIN);
			updateMainConfigFile(configFile, configEnvironment, ConfigScope.HIGHEST);
			configFile.finishReload(configEnvironment, ConfigScope.HIGHEST);
		}
	}

	/**
	 * Reloads all matching {@link ConfigFile}s.
	 * @param resourceManager the current {@link ResourceManager}
	 * @param environment the current environment
	 * @param scope the definition scope
	 */
	public static void loadConfigs(ResourceManager resourceManager, ConfigEnvironment environment, ConfigScope scope) {
		currentResourceManager.set(resourceManager);
		Collection<ConfigFile> configFiles = TweedRegistry.getConfigFiles();
		for(ConfigFile configFile : configFiles) {
			configFile.reset(environment, scope);
			configFile.load(readMainConfigFile(configFile).asObject(), environment, scope, ConfigOrigin.MAIN);
            updateMainConfigFile(configFile, environment, scope);
			try {
				List<Resource> resources = resourceManager.getAllResources(configFile.getFileIdentifier());
				for(Resource resource : resources) {
					configFile.load(resource, environment, scope, ConfigOrigin.DATAPACK);
				}
			} catch (Exception ignored) {}
			configFile.finishReload(environment, scope);
			if(environment.triggers(ConfigEnvironment.SERVER)) {
				configFile.syncToClients(ConfigEnvironment.SYNCED, scope, ConfigOrigin.DATAPACK);
			}
		}
		currentResourceManager.set(null);
	}

	/**
	 * Get the resource manager that's currently in use for reloading.
	 * @return The resource manager in use on this thread
	 */
	@ApiStatus.Experimental
	public static ResourceManager getCurrentResourceManager() {
		return currentResourceManager.get();
	}

	/**
	 * Updates the main file of a {@link ConfigFile}
	 * @param configFile the config file
	 * @param environment the current environment
	 * @param scope the definition scope
	 */
	public static <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void updateMainConfigFile(ConfigFile configFile, ConfigEnvironment environment, ConfigScope scope) {
        O dataObject = ConfigLoader.<V, L, O>readMainConfigFile(configFile).asObject();
        configFile.write(dataObject, environment, scope);
		File mainConfigFile = getMainConfigFile(configFile);
		//noinspection ResultOfMethodCallIgnored
		mainConfigFile.toPath().getParent().toFile().mkdirs();
		try {
			FileOutputStream outputStream = new FileOutputStream(mainConfigFile);
			configFile.<V, L, O>getDataSerializer().write(outputStream, dataObject);
            outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			Tweed.LOGGER.error("Failed to update config file " + configFile.getFileName());
		}
	}

	/**
	 * Reads data from the main config file
	 * @param configFile the config file
	 * @return the read in data
	 */
	public static <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	DataObject<V, L, O> readMainConfigFile(ConfigFile configFile) {
		File mainConfig = getMainConfigFile(configFile);
		if(mainConfig.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(mainConfig);
				O dataObject = configFile.<V, L, O>getDataSerializer().read(inputStream);
                inputStream.close();
                if(dataObject == null) {
                	Tweed.LOGGER.error("Failed to read config file " + configFile.getFileName());
					return configFile.<V, L, O>getDataSerializer().newObject();
				}
				return dataObject;
			} catch (Exception ignored) {
				Tweed.LOGGER.error("Failed to read config file " + configFile.getFileName());
			}
		}
		return configFile.<V, L, O>getDataSerializer().newObject();
	}

	/**
	 * Writes the current data to the main config file
	 * @param configFile the config file
	 * @param environment the current environment
	 * @param scope the definition scope
	 */
	public static  <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void writeMainConfigFile(ConfigFile configFile, ConfigEnvironment environment, ConfigScope scope) {
		File mainConfigFile = getMainConfigFile(configFile);
		//noinspection ResultOfMethodCallIgnored
		mainConfigFile.toPath().getParent().toFile().mkdirs();
		try {
			FileOutputStream outputStream = new FileOutputStream(mainConfigFile);
			configFile.<V, L, O>getDataSerializer().write(outputStream, configFile.write(configFile.<V, L, O>getDataSerializer().newObject(), environment, scope));
            outputStream.close();
		} catch (Exception e) {
			Tweed.LOGGER.error("Failed to load config file " + configFile.getFileName());
			e.printStackTrace();
		}
	}

	/**
	 * Gets the main file for a {@link ConfigFile}
	 * @param configFile the config file
	 * @return a {@link File} for the main config file
	 */
	public static File getMainConfigFile(ConfigFile configFile) {
		return new File(Tweed.mainConfigDirectory, configFile.getFileName());
	}
}
