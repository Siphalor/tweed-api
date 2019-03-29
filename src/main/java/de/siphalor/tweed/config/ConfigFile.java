package de.siphalor.tweed.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.siphalor.tweed.Core;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.hjson.JsonObject;
import org.hjson.JsonType;
import org.hjson.JsonValue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A configuration file.
 * @see ConfigRegistry#registerConfigFile(String)
 */
public class ConfigFile {
	private String fileName;
	private BiConsumer<ConfigEnvironment, ConfigDefinitionScope> reloadListener = null;

	protected Map<String, ConfigEntry> entries = new HashMap<>();
	protected BiMap<String, ConfigCategory> categories = HashBiMap.create();

	protected ConfigFile(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Adds a new reload listener.
	 *
	 * This gets called after all reloading of sub-entries is done for the specific reload point.
	 * @param listener a {@link BiConsumer} accepting the used {@link ConfigEnvironment} and {@link ConfigDefinitionScope}
	 * @return the current config file (for chain calls)
	 */
	public ConfigFile setReloadListener(BiConsumer<ConfigEnvironment, ConfigDefinitionScope> listener) {
		reloadListener = listener;
		return this;
	}

	protected void finishReload(ConfigEnvironment environment, ConfigDefinitionScope definitionScope) {
		if(reloadListener != null)
			reloadListener.accept(environment, definitionScope);
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
	 * @param name the property name to use
	 * @param entry the entry itself
	 * @return the entry (for chain calls)
	 */
	public <T extends ConfigEntry> T register(String name, T entry) {
		entries.put(name, entry);
		return entry;
	}

	/**
	 * Used to append information to categories
	 * @param path the path to the category (see {@link Core#HJSON_PATH_DELIMITER})
	 * @return the category
	 */
	public ConfigCategory category(String path) {
		ConfigCategory configCategory = new ConfigCategory(this);
		categories.put(path, configCategory);
		return configCategory;
	}

	/**
	 * Constructs a {@link JsonObject} for writing it to the {@link Core#mainConfigDirectory}
	 * @param environment the current environment
	 * @param definitionScope the current definition scope
	 * @return the new {@link JsonObject}
	 */
	public JsonObject write(ConfigEnvironment environment, ConfigDefinitionScope definitionScope) {
		JsonObject jsonObject = new JsonObject();
		entry:
		for(Map.Entry<String, ConfigEntry> entry : entries.entrySet()) {
            if(!environment.matches(entry.getValue().getEnvironment()) || !entry.getValue().getDefinitionScope().isIn(definitionScope))
            	continue;
			String path = entry.getValue().categoryPath;
			String[] parts = StringUtils.split(path, Core.HJSON_PATH_DELIMITER);
			JsonObject parent = jsonObject;
			path = "";
			for(String part : parts) {
				path += path.equals("") ? part : ":" + part;
				if(part.length() > 0) {
					if(parent.get(part) == null) {
						JsonObject partObject = new JsonObject();
						if(categories.containsKey(path))
							partObject.setComment(categories.get(path).comment);
						parent.add(part, partObject);
					} else {
						if(parent.get(part).getType() != JsonType.OBJECT) {
							System.err.println("Internal error with config generation");
							continue entry;
						}
					}
					parent = (JsonObject) parent.get(part);
				}
			}
			entry.getValue().write(parent, entry.getKey());
		}
		return jsonObject;
	}

	/**
	 * Resets all entries to their default values
	 * @param environment The current {@link ConfigEnvironment}
	 * @param definitionScope The current {@link ConfigDefinitionScope}
	 */
	public void reset(ConfigEnvironment environment, ConfigDefinitionScope definitionScope) {
		for(ConfigEntry entry : entries.values()) {
			if(environment.matches(entry.getEnvironment()) && entry.getDefinitionScope().isIn(definitionScope))
			entry.reset();
		}
	}

	public void load(Resource resource, ConfigEnvironment environment, ConfigDefinitionScope definitionScope) {
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
        load(json.asObject(), environment, definitionScope);
	}

	public void load(JsonObject json, ConfigEnvironment environment, ConfigDefinitionScope definitionScope) {
		entry:
		for(Map.Entry<String, ConfigEntry> entry : entries.entrySet()) {
			if(!environment.matches(entry.getValue().getEnvironment()))
				continue;
			if(!entry.getValue().getDefinitionScope().isIn(definitionScope))
				continue;
			String path = entry.getValue().categoryPath;
			JsonObject parent = json;
			String[] parts = StringUtils.split(path, Core.HJSON_PATH_DELIMITER);
			for(String part : parts) {
				if(!part.equals("")) {
					if(parent.get(part) == null)
						continue entry;
					parent = parent.get(part).asObject();
				}
			}
			if(parent.get(entry.getKey()) != null)
				entry.getValue().read(parent.get(entry.getKey()));
		}
	}

}
