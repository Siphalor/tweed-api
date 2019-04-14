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
	 * @param definitionScope the definition scope
	 */
	public static void loadConfigs(ResourceManager resourceManager, ConfigEnvironment environment, ConfigScope definitionScope) {
		Collection<ConfigFile> configFiles = TweedRegistry.getConfigFiles();
		for(ConfigFile configFile : configFiles) {
			configFile.reset(environment, definitionScope);
			File mainConfig = new File(Core.mainConfigDirectory, configFile.getFileName());
			if(mainConfig.exists()) {
				try {
					FileReader reader = new FileReader(mainConfig);
					configFile.load((JsonObject) JsonValue.readHjson(reader), environment, definitionScope);
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//noinspection ResultOfMethodCallIgnored
			new File(Core.mainConfigDirectory).mkdirs();
			try {
				FileWriter writer = new FileWriter(mainConfig);
				configFile.write(environment, definitionScope).writeTo(writer, Core.HJSON_OPTIONS);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				List<Resource> resources = resourceManager.getAllResources(configFile.getFileIdentifier());
				for(Resource resource : resources) {
					configFile.load(resource, environment, definitionScope);
				}
			} catch (IOException ignored) {}
			configFile.finishReload(environment, definitionScope);
			if(environment.isContainedIn(ConfigEnvironment.SERVER)) {
				configFile.syncToClients(definitionScope);
			}
		}
	}
}
