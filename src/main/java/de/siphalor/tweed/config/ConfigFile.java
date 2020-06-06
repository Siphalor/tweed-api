package de.siphalor.tweed.config;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.entry.ConfigEntry;
import de.siphalor.tweed.config.fixers.ConfigEntryFixer;
import de.siphalor.tweed.data.DataObject;
import de.siphalor.tweed.data.DataValue;
import de.siphalor.tweed.data.serializer.ConfigDataSerializer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.Resource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

/**
 * A configuration file.
 * @see TweedRegistry#registerConfigFile(String, ConfigDataSerializer)
 */
@SuppressWarnings("unused")
public class ConfigFile {
	private String name;
	private BiConsumer<ConfigEnvironment, ConfigScope> reloadListener = null;
	private Queue<Pair<String, ConfigEntryFixer>> configEntryFixers;
	private ConfigDataSerializer<?> dataSerializer;

	private Map<Class<? extends Annotation>, Annotation> tailorAnnotations;

	protected ConfigCategory rootCategory;

	protected ConfigFile(String name, ConfigDataSerializer<?> dataSerializer) {
		this(name, dataSerializer, new ConfigCategory());
	}

	protected ConfigFile(String name, ConfigDataSerializer<?> dataSerializer, ConfigCategory rootCategory) {
		this.name = name;
		this.rootCategory = rootCategory;
		this.dataSerializer = dataSerializer;
		configEntryFixers = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Adds a new reload listener.
	 *
	 * This gets called after all reloading of sub-entries is done for the specific reload point.
	 * @param listener a {@link BiConsumer} accepting the used {@link ConfigEnvironment} and {@link ConfigScope}
	 * @return the current config file (for chain calls)
	 */
	public ConfigFile setReloadListener(BiConsumer<ConfigEnvironment, ConfigScope> listener) {
		reloadListener = listener;
		return this;
	}

	public ConfigDataSerializer<?> getDataSerializer() {
		return dataSerializer;
	}

	public void finishReload(ConfigEnvironment environment, ConfigScope scope) {
		Tweed.LOGGER.info("Reloaded configs for " + name + " (" + environment.name().toLowerCase(Locale.ENGLISH) + "/" + scope.name().toLowerCase(Locale.ENGLISH) + ")");
		if(reloadListener != null)
			reloadListener.accept(environment, scope);
	}

	/**
	 * Gets the file identifier used in datapacks.
	 * @return the identifier
	 */
	public Identifier getFileIdentifier() {
		return new Identifier(Tweed.MOD_ID, "config/" + getFileName());
	}

	/**
	 * Gets the file id
	 * @return the file id <b>with extension</b>
	 * @see ConfigFile#getName()
	 */
	public String getFileName() {
		return name + "." + dataSerializer.getFileExtension();
	}

	/**
	 * Gets the id
	 * @return the (file) id <b>without extension</b>
	 * @see ConfigFile#getFileName()
	 */
	public String getName() {
		return name.replace('/', '.');
	}

	public ConfigCategory getRootCategory() {
		return rootCategory;
	}

	/**
	 * Sets the root category. <b>Use with extreme caution!</b>
	 * @param rootCategory the new root category
	 */
	public void setRootCategory(ConfigCategory rootCategory) {
		this.rootCategory = rootCategory;
	}

	/**
	 * Registers a new {@link ConfigEntry}.
	 * @param name the property id or path of the entry ({@link Tweed#PATH_DELIMITER}
	 * @param entry the entry itself
	 * @return the entry (for chain calls) or <i>null</i> if the path to the entry is invalid
	 */
	public <T extends ConfigEntry<?>> T register(String name, T entry) {
        String[] parts = StringUtils.split(name, Tweed.PATH_DELIMITER);
        if(parts.length == 1)
        	rootCategory.register(name, entry);
        else {
        	ConfigCategory category = rootCategory;
        	for(int i = 0; i < parts.length - 1; i++) {
        		ConfigEntry<?> iEntry = category.entries.get(parts[i]);
                if(!(iEntry instanceof ConfigCategory)) {
                	return null;
				}
				category = (ConfigCategory) iEntry;
			}
        	category.register(parts[parts.length - 1], entry);
		}
		return entry;
	}

	/**
	 * Registers a new {@link ConfigEntryFixer}
	 * @param path the id/path of the value to be fixed
	 * @param configEntryFixer a fixer
	 */
	public void register(String path, ConfigEntryFixer configEntryFixer) {
		configEntryFixers.add(new Pair<>(path, configEntryFixer));
	}

	/**
	 * Writes to the {@link DataObject} for handing it to the {@link Tweed#mainConfigDirectory}
	 *
	 * @param dataObject the target data
	 * @param environment the current environment
	 * @param scope the current definition scope
	 * @return the new {@link DataObject}
	 */
	public DataObject<?> write(DataObject<?> dataObject, ConfigEnvironment environment, ConfigScope scope) {
		fixConfig(dataObject);
		rootCategory.write(dataObject, "", environment, scope);
		return dataObject;
	}

	/**
	 * Resets all entries to their default values
	 * @param environment The current {@link ConfigEnvironment}
	 * @param scope The current {@link ConfigScope}
	 */
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
        rootCategory.reset(environment, scope);
	}

	public void fixConfig(DataObject<?> dataObject) {
		configEntryFixers.forEach(stringConfigEntryFixerPair -> {
			String[] parts = StringUtils.split(stringConfigEntryFixerPair.getLeft(), Tweed.PATH_DELIMITER);
			DataObject<?> location = dataObject;
			for(int i = 0; i < parts.length - 1; i++) {
				DataValue<?> dataValue = location.get(parts[i]);
				if(dataValue == null || !dataValue.isObject())
					return;
				location = dataValue.asObject();
			}
			stringConfigEntryFixerPair.getRight().fix(location, parts[parts.length - 1], dataObject);
		});
	}

	public void load(Resource resource, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		DataObject<?> dataObject = dataSerializer.read(resource.getInputStream());
		try {
			resource.close();
		} catch (IOException e) {
			Tweed.LOGGER.error("Failed to close config resource after reading it in resource pack: " + resource.getResourcePackName());
			e.printStackTrace();
		}
		if(dataObject != null) {
			load(dataObject, environment, scope, origin);
		}
	}

	public void load(DataObject<?> dataObject, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		fixConfig(dataObject);

		try {
			rootCategory.read(dataObject, environment, scope, origin);
		} catch (ConfigReadException e) {
            Tweed.LOGGER.error("The config file " + name + "." + dataSerializer.getFileExtension() + " must contain an object!");
		}
	}

	public void syncToClients(ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeEnumConstant(origin);
		packetByteBuf.writeString(name);
		write(packetByteBuf, environment, scope, origin);

		Tweed.MINECRAFT_SERVERS.stream().flatMap(PlayerStream::all).forEach(serverPlayerEntity -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(serverPlayerEntity, Tweed.CONFIG_SYNC_S2C_PACKET, packetByteBuf));
	}

	public void syncToClient(ServerPlayerEntity playerEntity, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeEnumConstant(origin);
		packetByteBuf.writeString(name);
		write(packetByteBuf, environment, scope, origin);

		ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, Tweed.CONFIG_SYNC_S2C_PACKET, packetByteBuf);
	}

	public void syncToServer(ConfigEnvironment environment, ConfigScope scope) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeString(name);
		packetByteBuf.writeEnumConstant(environment);
		packetByteBuf.writeEnumConstant(scope);
		write(packetByteBuf, environment, scope, ConfigOrigin.MAIN);

		ClientSidePacketRegistry.INSTANCE.sendToServer(Tweed.TWEED_CLOTH_SYNC_C2S_PACKET, packetByteBuf);
	}

	protected void write(PacketByteBuf buffer, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		rootCategory.write(buffer, environment, scope, origin);
	}

	public void read(PacketByteBuf buffer, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		rootCategory.read(buffer, environment, scope, origin);
		if(reloadListener != null)
			reloadListener.accept(environment, scope);
	}

	/**
	 * Convenience function for <code>getRootCategory().setBackgroundTexture(...)</code>.
	 * @param path the resource path to the background texture
	 * @see ConfigCategory#setBackgroundTexture(Identifier)
	 */
	public ConfigFile setBackgroundTexture(Identifier path) {
		rootCategory.setBackgroundTexture(path);
		return this;
	}

	/**
	 * Convenience function for <code>getRootCategory().setComment(...)</code>.
	 * @param comment the comment
	 * @see ConfigCategory#setComment(String)
	 */
	public ConfigFile setComment(String comment) {
		rootCategory.setComment(comment);
		return this;
	}

	/**
	 * Sets the default environment for config entries. Equivalent to <code>getRootCategory().setEnvironment(...)</code>
	 * @param environment the environment
	 */
	public ConfigFile setEnvironment(ConfigEnvironment environment) {
		rootCategory.setEnvironment(environment);
		return this;
	}

	/**
	 * Sets the default scope for config entries. Equivalent to <code>getRootCategory().setScope(...)</code>
	 * @param scope the scope
	 */
	public ConfigFile setScope(ConfigScope scope) {
		rootCategory.setScope(scope);
		return this;
	}

	public void addTailorData(Annotation annotation) {
		if (tailorAnnotations == null) {
			tailorAnnotations = new HashMap<>();
		}
		tailorAnnotations.put(annotation.annotationType(), annotation);
	}

	public void addTailorAnnotations(Collection<Annotation> annotations) {
		if (tailorAnnotations == null) {
			tailorAnnotations = new HashMap<>();
		}
		annotations.forEach(a -> tailorAnnotations.put(a.annotationType(), a));
	}

	public void addTailorAnnotations(Annotation... annotations) {
		if (tailorAnnotations == null) {
			tailorAnnotations = new HashMap<>();
		}
		for (Annotation a : annotations) {
			tailorAnnotations.put(a.annotationType(), a);
		}
	}

	public <T extends Annotation> T getTailorAnnotation(Class<T> clazz) {
		if (clazz == null || tailorAnnotations == null) {
			return null;
		}
		//noinspection unchecked
		return (T) tailorAnnotations.get(clazz);
	}
}
