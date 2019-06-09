package de.siphalor.tweed.config;

import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.config.entry.AbstractBasicEntry;
import de.siphalor.tweed.config.entry.ConfigEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.hjson.CommentStyle;
import org.hjson.CommentType;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigCategory extends AbstractBasicEntry<ConfigCategory> {

	protected Map<String, ConfigEntry> entries = new LinkedHashMap<>();
	protected Identifier backgroundTexture;

	/**
	 * Adds a new entry to the category
	 * @param name the key used in the HJSON
	 * @param configEntry the entry to add
	 * @see ConfigFile#register(String, ConfigEntry)
	 */
	public <T extends ConfigEntry> T register(String name, T configEntry) {
		entries.put(name, configEntry);
		return configEntry;
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
		entryStream(environment, scope).forEach(entry -> entry.getValue().reset(environment, scope));
	}

	@Override
	public String getDescription() {
		return comment;
	}

	/**
	 * Sets the background texture for a possible GUI
	 * @param backgroundTexture an identifier to that texture
	 * @return this category for chain calls
	 */
	public ConfigCategory setBackgroundTexture(Identifier backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
		return this;
	}

	/**
	 * Gets the background texture identifier (<b>may be null!</b>)
	 * @return an identifier for the background texture or <b>null</b>
	 */
	public Identifier getBackgroundTexture() {
		return backgroundTexture;
	}

	@Override
	public ConfigEnvironment getEnvironment() {
		if(entries.isEmpty()) return ConfigEnvironment.UNIVERSAL;
		Iterator<ConfigEntry> iterator = entries.values().iterator();
		ConfigEnvironment environment = iterator.next().getEnvironment();
		while(iterator.hasNext()) {
			ConfigEnvironment itEnvironment = iterator.next().getEnvironment();
            while(!environment.contains(itEnvironment))
            	environment = environment.parent;
		}
		return environment;
	}

	@Override
	public void read(JsonValue json, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		if(!json.isObject()) {
			throw new ConfigReadException("The entry should be an object (category)");
		}
		JsonObject jsonObject = json.asObject();
		entryStream(environment, scope).filter(entry -> jsonObject.get(entry.getKey()) != null).forEach(entry -> {
			JsonValue value = jsonObject.get(entry.getKey());
			try {
				entry.getValue().applyPreConstraints(value);
			} catch (ConstraintException e) {
				System.err.println("Error reading " + entry.getKey() + " in pre-constraints:");
				e.printStackTrace();
				if(e.fatal)
					return;
			}
			try {
				entry.getValue().read(value, environment, scope, origin);
			} catch (ConfigReadException e) {
				System.err.println("Error reading " + entry.getKey() + ":");
				e.printStackTrace();
			}
			try {
				entry.getValue().applyPostConstraints(value);
			} catch (ConfigReadException e) {
                System.err.println("Error reading " + entry.getKey() + " in post-constraints:");
                e.printStackTrace();
			}
		});
	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		while(buf.readBoolean()) {
			ConfigEntry entry = entries.get(buf.readString(32767));
			if(entry != null)
				entry.read(buf, environment, scope, origin);
			else
				return;
		}
	}

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		entryStream(environment, scope).forEach(entry -> {
			buf.writeBoolean(true);
			buf.writeString(entry.getKey());
			entry.getValue().write(buf, environment, scope, origin);
		});
		buf.writeBoolean(false);
	}

	@Override
	public void write(JsonObject jsonObject, String key, ConfigEnvironment environment, ConfigScope scope) {
		JsonObject categoryObject;
		if(key.equals("")) {
			categoryObject = jsonObject;
		} else if(jsonObject.get(key) == null) {
			categoryObject = new JsonObject();
			jsonObject.add(key, categoryObject);
		} else {
			categoryObject = jsonObject.get(key).asObject();
		}
		if(!comment.equals(""))
			categoryObject.setComment(CommentType.BOL, CommentStyle.LINE, getComment());
		entryStream(environment, scope).forEach(entry -> entry.getValue().write(categoryObject, entry.getKey(), environment, scope));
	}

	public Stream<Map.Entry<String, ConfigEntry>> entryStream() {
		return entries.entrySet().stream();
	}

	@Deprecated
	public Stream<Map.Entry<String, ConfigEntry>> sortedEntryStream() {
		return entryStream().sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
	}

	public Stream<Map.Entry<String, ConfigEntry>> entryStream(ConfigEnvironment environment, ConfigScope scope) {
		return entryStream().filter(entry -> environment.contains(entry.getValue().getEnvironment()) && scope.triggers(entry.getValue().getScope()));
	}

	@Deprecated
	public Stream<Map.Entry<String, ConfigEntry>> sortedEntryStream(ConfigEnvironment environment, ConfigScope scope) {
		return entryStream(environment, scope).sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
	}
}
