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

package de.siphalor.tweed5.config;

import de.siphalor.tweed5.Tweed;
import de.siphalor.tweed5.TweedRegistries;
import de.siphalor.tweed5.data.AnnotatedDataValue;
import de.siphalor.tweed5.data.DataSerializer;
import de.siphalor.tweed5.reload.ReloadContext;
import de.siphalor.tweed5.reload.ReloadEnvironment;
import de.siphalor.tweed5.reload.ReloadScope;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.util.List;

/**
 * Used to reload the {@link ConfigFile}s.
 */
public final class ConfigLoader {
	private static final ThreadLocal<ResourceManager> currentResourceManager = new ThreadLocal<>();
	private static final ObjectArraySet<ConfigFile> initiallyReloadedFiles = new ObjectArraySet<>();

	public static void initialReload(ReloadEnvironment reloadEnvironment) {
		for (ConfigFile configFile : TweedRegistries.CONFIG_FILES.getValues()) {
			initialReload(configFile, reloadEnvironment);
		}
	}

	public static void initialReload(ConfigFile configFile, ReloadEnvironment environment) {
		if (initiallyReloadedFiles.contains(configFile)) {
			return;
		}
		initiallyReloadedFiles.add(configFile);

		Tweed.runEntryPoints();

		ReloadContext reloadContext = ReloadContext.file(environment, ReloadScope.HIGHEST, true, configFile);

		DataSerializer<Object> serializer = configFile.getDataSerializer();
		configFile.load(serializer, readFileOrEmpty(getMainConfigFile(configFile), serializer), reloadContext);
		updateMainConfigFile(configFile, environment, ReloadScope.HIGHEST);
		configFile.finishReload(reloadContext);
	}

	/**
	 * Reloads all matching {@link ConfigFile}s.
	 * @param resourceManager the {@link ResourceManager} to use
	 * @param environment the current environment
	 * @param scope the current scope
	 */
	public static void reloadAll(ResourceManager resourceManager, ReloadEnvironment environment, ReloadScope scope) {
		currentResourceManager.set(resourceManager);
		for(ConfigFile configFile : TweedRegistries.CONFIG_FILES.getValues()) {
			reloadSimple(configFile, resourceManager, environment, scope);
		}
		currentResourceManager.set(null);
	}

	/**
	 * Reloads a single {@link ConfigFile}.
	 * @param configFile The config to reload
	 * @param resourceManager the {@link ResourceManager} to use
	 * @param environment the current environment
	 * @param scope the current scope
	 */
	public static void reload(ConfigFile configFile, ResourceManager resourceManager, ReloadEnvironment environment, ReloadScope scope) {
		currentResourceManager.set(resourceManager);
		reloadSimple(configFile, resourceManager, environment, scope);
		currentResourceManager.set(null);
	}

	private static void reloadSimple(ConfigFile configFile, ResourceManager resourceManager, ReloadEnvironment environment, ReloadScope scope) {
		try {
			configFile.reset(environment, scope);
			DataSerializer<Object> serializer = configFile.getDataSerializer();
			AnnotatedDataValue<Object> data = readFileOrEmpty(getMainConfigFile(configFile), serializer);
			ReloadContext mainReloadContext = ReloadContext.file(environment, scope, true, configFile);
			configFile.load(serializer, data, mainReloadContext);
			updateMainConfigFile(configFile, environment, scope);

			try {
				ReloadContext packReloadContext = mainReloadContext.withMainOrigin(false);

				List<Resource> resources = resourceManager.getAllResources(configFile.getFileIdentifier());
				for (Resource resource : resources) {
					try {
						InputStream inputStream = resource.getInputStream();
						configFile.load(inputStream, packReloadContext);
						inputStream.close();
					} catch (IOException e) {
						Tweed.LOGGER.error("Failed to load config file " + configFile.getFileName() + " from resource pack " + resource.getResourcePackName(), e);
					}
				}
			} catch (Exception ignored) {
			}

			configFile.finishReload(mainReloadContext);

			if (environment.triggers(ReloadEnvironment.SERVER)) {
				configFile.syncToClients(mainReloadContext.withMainOrigin(false));
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load config file " + configFile.getFileIdentifier(), e);
		}
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
	public static <V> void updateMainConfigFile(ConfigFile configFile, ReloadEnvironment environment, ReloadScope scope) {
		DataSerializer<V> serializer = configFile.getDataSerializer();
		File mainConfigFile = getMainConfigFile(configFile);
		AnnotatedDataValue<V> data = ConfigLoader.readFileOrEmpty(mainConfigFile, serializer);
        configFile.write(serializer, data, ReloadContext.file(environment, scope, true, configFile));
		//noinspection ResultOfMethodCallIgnored
		mainConfigFile.toPath().getParent().toFile().mkdirs();
		try {
			FileOutputStream outputStream = new FileOutputStream(mainConfigFile);
			serializer.write(outputStream, data);
            outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			Tweed.LOGGER.error("Failed to update config file " + configFile.getFileName());
		}
	}

	/**
	 * Reads data from the main config file
	 * @param file the file to read from
	 * @param serializer the serializer to use
	 * @return the read in data
	 */
	public static <V> AnnotatedDataValue<V> readFileOrEmpty(File file, DataSerializer<V> serializer) {
		if (file.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				AnnotatedDataValue<V> data = serializer.read(inputStream);
                inputStream.close();
                if (data == null) {
                	Tweed.LOGGER.error("Failed to read config file " + file.getPath());
					return AnnotatedDataValue.of(serializer.newObject().getValue());
				}
				return data;
			} catch (Exception ignored) {
				Tweed.LOGGER.error("Failed to read config file " + file.getPath());
			}
		}
		return AnnotatedDataValue.of(serializer.newObject().getValue());
	}

	/**
	 * Writes the current data to the main config file
	 * @param configFile the config file
	 * @param environment the current environment
	 * @param scope the definition scope
	 */
	public static <V> void writeMainConfigFile(ConfigFile configFile, ReloadEnvironment environment, ReloadScope scope) {
		File mainConfigFile = getMainConfigFile(configFile);
		//noinspection ResultOfMethodCallIgnored
		mainConfigFile.toPath().getParent().toFile().mkdirs();
		try {
			FileOutputStream outputStream = new FileOutputStream(mainConfigFile);
			DataSerializer<V> serializer = configFile.getDataSerializer();
			serializer.write(outputStream, configFile.write(serializer, AnnotatedDataValue.of(serializer.newObject().getValue()), ReloadContext.file(environment, scope, true, configFile)));
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
