package de.siphalor.tweed.config;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.entry.ConfigEntry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.StringUtils;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;

/**
 * A configuration file.
 * @see TweedRegistry#registerConfigFile(String)
 */
public class ConfigFile {
	private String fileName;
	private BiConsumer<ConfigEnvironment, ConfigScope> reloadListener = null;

	protected ConfigCategory mainCategory;

	protected ConfigFile(String fileName) {
		this.fileName = fileName;
		mainCategory = new ConfigCategory();
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

	protected void finishReload(ConfigEnvironment environment, ConfigScope scope) {
		if(reloadListener != null)
			reloadListener.accept(environment, scope);
	}

	/**
	 * Gets the file identifier used in datapacks.
	 * @return the identifier
	 */
	public Identifier getFileIdentifier() {
		return new Identifier(Core.MODID, "config/" + fileName + ".hjson");
	}

	/**
	 * Gets the file name
	 * @return the file name <b>with extension</b>
	 */
	public String getFileName() {
		return fileName + ".hjson";
	}

	/**
	 * Registers a new {@link ConfigEntry}.
	 * @param path the property path of the entry ({@link Core#HJSON_PATH_DELIMITER}
	 * @param entry the entry itself
	 * @return the entry (for chain calls) or <i>null</i> if the path to the entry is invalid
	 */
	public <T extends ConfigEntry> T register(String path, T entry) {
        String[] parts = StringUtils.split(path, Core.HJSON_PATH_DELIMITER);
        if(parts.length == 1)
        	mainCategory.register(path, entry);
        else {
        	ConfigCategory category = mainCategory;
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
	 * Constructs a {@link JsonObject} for writing it to the {@link Core#mainConfigDirectory}
	 * @param environment the current environment
	 * @param scope the current definition scope
	 * @return the new {@link JsonObject}
	 */
	public JsonObject write(ConfigEnvironment environment, ConfigScope scope) {
		JsonObject jsonObject = new JsonObject();
		mainCategory.write(jsonObject, "", environment, scope);
		return jsonObject;
	}

	/**
	 * Resets all entries to their default values
	 * @param environment The current {@link ConfigEnvironment}
	 * @param scope The current {@link ConfigScope}
	 */
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
        mainCategory.reset(environment, scope);
	}

	public void load(Resource resource, ConfigEnvironment environment, ConfigScope scope) {
		JsonValue json;
		try {
			json = JsonValue.readHjson(new InputStreamReader(resource.getInputStream()));
		} catch (IOException e) {
            System.err.println("Couldn't load config file '" + fileName + "'");
            return;
		}
        if(!json.isObject()) {
        	System.err.println("Config files should contain a hjson object!");
        	return;
        }
        load(json.asObject(), environment, scope);
	}

	public void load(JsonObject json, ConfigEnvironment environment, ConfigScope scope) {
		try {
			mainCategory.read(json, environment, scope);
		} catch (ConfigReadException e) {
            System.err.println("The config file " + fileName + ".hjson must contain an object!");
		}
	}

	public void syncToClients(ConfigScope scope) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeString(this.fileName);
		mainCategory.write(packetByteBuf);

		PlayerStream.all(Core.getMinecraftServer()).forEach(serverPlayerEntity -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(serverPlayerEntity, Core.CONFIG_SYNC_PACKET, packetByteBuf));
	}
}
