package de.siphalor.tweed.config;

import de.siphalor.tweed.config.annotated.ATweedConfig;
import de.siphalor.tweed.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed.data.serializer.GsonSerializer;
import de.siphalor.tweed.data.serializer.HjsonSerializer;
import de.siphalor.tweed.data.serializer.JanksonSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to register {@link ConfigFile}s.
 */
public class TweedRegistry {
	private static ArrayList<ConfigFile> configFiles = new ArrayList<>();
	private static final Map<String, ConfigDataSerializer<?>> SERIALIZERS = new HashMap<>();

	public static ConfigFile registerConfigFile(String fileName) {
		return registerConfigFile(fileName, HjsonSerializer.INSTANCE);
	}

	/**
	 * Registers a new {@link ConfigFile}.
	 * @param fileName the file id which is used (no extension; no subdirectories for now)
	 * @param dataSerializer a serializer for this config file
	 * @return the new {@link ConfigFile}
	 */
	public static ConfigFile registerConfigFile(String fileName, ConfigDataSerializer<?> dataSerializer) {
        ConfigFile configFile = new ConfigFile(fileName, dataSerializer);
        configFiles.add(configFile);
        return configFile;
	}

	public static ConfigFile registerPOJO(String fileName, Object pojo) {
		ConfigCategory configCategory = POJOConverter.toCategory(pojo);
		if (configCategory != null) {
			ConfigDataSerializer<?> configDataSerializer = null;
			if (pojo.getClass().isAnnotationPresent(ATweedConfig.class)) {
				ATweedConfig aTweedConfig = pojo.getClass().getAnnotation(ATweedConfig.class);
				configDataSerializer = SERIALIZERS.get(aTweedConfig.serializer());
				configCategory.setScope(aTweedConfig.scope());
				configCategory.setEnvironment(aTweedConfig.environment());
			}
			if (configDataSerializer == null) {
				configDataSerializer = HjsonSerializer.INSTANCE;
			}
			ConfigFile configFile = new ConfigFile(fileName, configDataSerializer, configCategory);
			configFiles.add(configFile);
			return configFile;
		}
		return null;
	}

	/**
	 * Gets a collection of all registered {@link ConfigFile}s.
	 * @return a collection of {@link ConfigFile}s
	 * @see #registerConfigFile(String, ConfigDataSerializer)
	 */
	public static ArrayList<ConfigFile> getConfigFiles() {
		return configFiles;
	}

	public static void registerDataSerializer(String id, ConfigDataSerializer<?> configDataSerializer) {
		SERIALIZERS.put(id, configDataSerializer);
	}

	static {
		SERIALIZERS.put("gson", GsonSerializer.INSTANCE);
		SERIALIZERS.put("hjson", HjsonSerializer.INSTANCE);
		SERIALIZERS.put("jankson", JanksonSerializer.INSTANCE);
	}
}
