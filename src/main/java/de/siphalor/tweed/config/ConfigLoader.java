package de.siphalor.tweed.config;

import de.siphalor.tweed.Core;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Used to reload the {@link ConfigFile}s.
 */
public final class ConfigLoader {
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
				configFile.syncToClients(ConfigEnvironment.SYNCED, scope);
			}
		}
	}

	public static void updateMainConfigFile(ConfigFile configFile, ConfigEnvironment environment, ConfigScope scope) {
        JsonObject jsonObject = readMainConfigFile(configFile);
        configFile.write(jsonObject, environment, scope);
		//noinspection ResultOfMethodCallIgnored
		new File(Core.mainConfigDirectory).mkdirs();
		try {
			FileWriter writer = new FileWriter(getMainConfigPath(configFile));
			jsonObject.writeTo(writer, configFile.getHjsonOptions());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JsonObject readMainConfigFile(ConfigFile configFile) {
		File mainConfig = getMainConfigPath(configFile);
		if(mainConfig.exists()) {
			try {
				FileReader reader = new FileReader(mainConfig);
				JsonObject json = (JsonObject) JsonValue.readHjson(reader);
				reader.close();
				return json;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new JsonObject();
	}

	public static void writeMainConfigFile(ConfigFile configFile, ConfigEnvironment environment, ConfigScope scope) {
		//noinspection ResultOfMethodCallIgnored
		new File(Core.mainConfigDirectory).mkdirs();
		try {
			FileWriter writer = new FileWriter(getMainConfigPath(configFile));
			configFile.write(new JsonObject(), environment, scope).writeTo(writer, configFile.getHjsonOptions());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getMainConfigPath(ConfigFile configFile) {
		return new File(Core.mainConfigDirectory, configFile.getFileName());
	}
}
