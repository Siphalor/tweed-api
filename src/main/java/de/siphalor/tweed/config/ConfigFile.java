package de.siphalor.tweed.config;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.entry.ConfigEntry;
import de.siphalor.tweed.config.fixers.ConfigEntryFixer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.resource.Resource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.hjson.HjsonOptions;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

/**
 * A configuration file.
 * @see TweedRegistry#registerConfigFile(String)
 */
public class ConfigFile {
	private String name;
	private BiConsumer<ConfigEnvironment, ConfigScope> reloadListener = null;
	private HjsonOptions hjsonOptions;
	private Queue<Pair<String, ConfigEntryFixer>> configEntryFixers;

	protected ConfigCategory rootCategory;

	protected ConfigFile(String name) {
		this.name = name;
		rootCategory = new ConfigCategory();
		hjsonOptions = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");
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

	public HjsonOptions getHjsonOptions() {
		return hjsonOptions;
	}

	public void setHjsonOptions(HjsonOptions hjsonOptions) {
		this.hjsonOptions = hjsonOptions;
	}

	public void finishReload(ConfigEnvironment environment, ConfigScope scope) {
		if(reloadListener != null)
			reloadListener.accept(environment, scope);
	}

	/**
	 * Gets the file identifier used in datapacks.
	 * @return the identifier
	 */
	public Identifier getFileIdentifier() {
		return new Identifier(Core.MODID, "config/" + name + ".hjson");
	}

	/**
	 * Gets the file name
	 * @return the file name <b>with extension</b>
	 * @see ConfigFile#getName()
	 */
	public String getFileName() {
		return name + ".hjson";
	}

	/**
	 * Gets the name
	 * @return the (file) name <b>without extension</b>
	 * @see ConfigFile#getFileName()
	 */
	public String getName() {
		return name.replace('/', '.');
	}

	public ConfigCategory getRootCategory() {
		return rootCategory;
	}

	/**
	 * Registers a new {@link ConfigEntry}.
	 * @param name the property name or path of the entry ({@link Core#HJSON_PATH_DELIMITER}
	 * @param entry the entry itself
	 * @return the entry (for chain calls) or <i>null</i> if the path to the entry is invalid
	 */
	public <T extends ConfigEntry> T register(String name, T entry) {
        String[] parts = StringUtils.split(name, Core.HJSON_PATH_DELIMITER);
        if(parts.length == 1)
        	rootCategory.register(name, entry);
        else {
        	ConfigCategory category = rootCategory;
        	for(int i = 0; i < parts.length - 1; i++) {
        		ConfigEntry iEntry = category.entries.get(parts[i]);
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
	 * @param path the name/path of the value to be fixed
	 * @param configEntryFixer a fixer
	 */
	public void register(String path, ConfigEntryFixer configEntryFixer) {
		configEntryFixers.add(new Pair<>(path, configEntryFixer));
	}

	/**
	 * Writes to the {@link JsonObject} for handing it to the {@link Core#mainConfigDirectory}
	 *
	 * @param jsonObject the target json
	 * @param environment the current environment
	 * @param scope the current definition scope
	 * @return the new {@link JsonObject}
	 */
	public JsonObject write(JsonObject jsonObject, ConfigEnvironment environment, ConfigScope scope) {
		fixConfig(jsonObject);
		rootCategory.write(jsonObject, "", environment, scope);
		return jsonObject;
	}

	/**
	 * Resets all entries to their default values
	 * @param environment The current {@link ConfigEnvironment}
	 * @param scope The current {@link ConfigScope}
	 */
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
        rootCategory.reset(environment, scope);
	}

	/**
     * @deprecated Is now done automatically
	 */
	@Deprecated
	public void triggerInitialLoad() {
		/*load(ConfigLoader.readMainConfigFile(this), ConfigEnvironment.SERVER, ConfigScope.GAME, ConfigOrigin.MAIN);
		ConfigLoader.updateMainConfigFile(this, ConfigEnvironment.SERVER, ConfigScope.GAME);
		finishReload(ConfigEnvironment.UNIVERSAL, ConfigScope.GAME);*/
	}

	public void fixConfig(JsonObject jsonObject) {
		configEntryFixers.forEach(stringConfigEntryFixerPair -> {
			String[] parts = StringUtils.split(stringConfigEntryFixerPair.getLeft(), Core.HJSON_PATH_DELIMITER);
			JsonObject location = jsonObject;
			for(int i = 0; i < parts.length - 1; i++) {
				JsonValue jsonValue = location.get(parts[i]);
				if(jsonValue == null || !jsonValue.isObject())
					return;
				location = jsonValue.asObject();
			}
			stringConfigEntryFixerPair.getRight().fix(location, parts[parts.length - 1], jsonObject);
		});
	}

	public void load(Resource resource, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		JsonValue json;
		try {
			json = JsonValue.readHjson(new InputStreamReader(resource.getInputStream()));
		} catch (Exception e) {
            System.err.println("Couldn't load config file '" + name + "'");
            e.printStackTrace();
            return;
		}
        if(!json.isObject()) {
        	System.err.println("Config files should contain an hjson object!");
        	return;
        }
        load(json.asObject(), environment, scope, origin);
	}

	public void load(JsonObject json, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		fixConfig(json);

		try {
			rootCategory.read(json, environment, scope, origin);
		} catch (ConfigReadException e) {
            System.err.println("The config file " + name + ".hjson must contain an object!");
		}
	}

	public void syncToClients(ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeEnumConstant(origin);
		packetByteBuf.writeString(name);
		write(packetByteBuf, environment, scope, origin);

		PlayerStream.all(Core.getMinecraftServer()).forEach(serverPlayerEntity -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(serverPlayerEntity, Core.CONFIG_SYNC_S2C_PACKET, packetByteBuf));
	}

	public void syncToClient(ServerPlayerEntity playerEntity, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeEnumConstant(origin);
		packetByteBuf.writeString(name);
		write(packetByteBuf, environment, scope, origin);

		ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, Core.CONFIG_SYNC_S2C_PACKET, packetByteBuf);
	}

	public void syncToServer(ConfigEnvironment environment, ConfigScope scope) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeString(name);
		packetByteBuf.writeEnumConstant(environment);
		packetByteBuf.writeEnumConstant(scope);
		write(packetByteBuf, environment, scope, ConfigOrigin.MAIN);

		ClientSidePacketRegistry.INSTANCE.sendToServer(Core.TWEED_CLOTH_SYNC_C2S_PACKET, packetByteBuf);
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
	public void setBackgroundTexture(Identifier path) {
		rootCategory.setBackgroundTexture(path);
	}

	/**
	 * Convenience function for <code>getRootCategory().setComment(...)</code>.
	 * @param comment the comment
	 * @see ConfigCategory#setComment(String)
	 */
	public void setComment(String comment) {
		rootCategory.setComment(comment);
	}
}
