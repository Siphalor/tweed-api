package de.siphalor.tweed.config;

import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.config.entry.AbstractBasicEntry;
import de.siphalor.tweed.config.entry.ConfigEntry;
import net.minecraft.util.PacketByteBuf;
import org.hjson.CommentStyle;
import org.hjson.CommentType;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigCategory extends AbstractBasicEntry<ConfigCategory> {

	protected Map<String, ConfigEntry> entries = new HashMap<>();

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
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope) {
		while(buf.readBoolean()) {
			entries.get(buf.readString()).read(buf, environment, scope);
		}
	}

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope) {
		entryStream(environment, scope).forEach(entry -> {
			buf.writeBoolean(true);
			buf.writeString(entry.getKey());
			entry.getValue().write(buf, environment, scope);
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
			categoryObject.setComment(CommentType.BOL, CommentStyle.LINE, comment);
		entryStream(environment, scope).forEach(entry -> entry.getValue().write(categoryObject, entry.getKey(), environment, scope));
	}

	public Stream<Map.Entry<String, ConfigEntry>> entryStream() {
		return entries.entrySet().stream();
	}

	public Stream<Map.Entry<String, ConfigEntry>> entryStream(ConfigEnvironment environment, ConfigScope scope) {
		return entryStream().filter(entry -> entry.getValue().getEnvironment().contains(environment) && entry.getValue().getScope().triggeredBy(scope));
	}
}
