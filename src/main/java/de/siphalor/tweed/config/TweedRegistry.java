package de.siphalor.tweed.config;

import com.mojang.serialization.Lifecycle;
import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed.data.serializer.GsonSerializer;
import de.siphalor.tweed.data.serializer.HjsonSerializer;
import de.siphalor.tweed.data.serializer.JanksonSerializer;
import de.siphalor.tweed.tailor.Tailor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.ArrayList;

/**
 * Used to register {@link ConfigFile}s.
 */
public class TweedRegistry {
	private static final ArrayList<ConfigFile> CONFIG_FILES = new ArrayList<>();
	/**
	 * This registry contains all of the known {@link ConfigDataSerializer}s.<br />
	 * By default available serializers are <code>gson</code>, <code>hjson</code> and <code>jankson</code>.
	 */
	public static final Registry<ConfigDataSerializer<?>> SERIALIZERS = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Tweed.MOD_ID, "serializers")), Lifecycle.experimental());
	/**
	 * This registry contains all of the known {@link Tailor}s.<br />
	 * By default only a serializer for the Cloth config UI is available as <code>tweed:cloth</code>.
	 */
	public static final Registry<Tailor> TAILORS = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Tweed.MOD_ID, "tailors")), Lifecycle.experimental());

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
        CONFIG_FILES.add(configFile);
        return configFile;
	}

	public static ConfigFile registerConfigPOJO(Object pojo, String modId) throws RuntimeException {
		ConfigFile configFile = POJOConverter.toConfigFile(pojo, modId);
		CONFIG_FILES.add(configFile);
		return configFile;
	}

	/**
	 * Gets a collection of all registered {@link ConfigFile}s.
	 * @return a collection of {@link ConfigFile}s
	 * @see #registerConfigFile(String, ConfigDataSerializer)
	 */
	public static ArrayList<ConfigFile> getConfigFiles() {
		return CONFIG_FILES;
	}

	static {
		Registry.register(SERIALIZERS, "gson", GsonSerializer.INSTANCE);
		Registry.register(SERIALIZERS, "hjson", HjsonSerializer.INSTANCE);
		Registry.register(SERIALIZERS, "jankson", JanksonSerializer.INSTANCE);
	}
}
