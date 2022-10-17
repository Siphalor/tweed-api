package de.siphalor.tweed4.data.jankson;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class JanksonObject implements DataObject<JsonElement, JanksonList, JanksonObject> {
	private final JsonObject jsonObject;

	public JanksonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JanksonObject() {
		this(new JsonObject());
	}

	@Override
	public @NotNull JsonElement getValue() {
		return jsonObject;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String getComment(String key) {
		return jsonObject.getComment(key);
	}

	@Override
	public void setComment(String key, String comment) {
		jsonObject.setComment(key, comment);
	}

	@Override
	public boolean has(String key) {
		return jsonObject.containsKey(key);
	}

	@Override
	public DataSerializer<JsonElement, JanksonList, JanksonObject> getSerializer() {
		return JanksonSerializer.INSTANCE;
	}

	@Override
	public int size() {
		return jsonObject.size();
	}

	@Override
	public boolean isEmpty() {
		return jsonObject.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return jsonObject.containsValue(value);
	}

	@Override
	public JsonElement get(Object key) {
		return jsonObject.get(key);
	}

	@Nullable
	@Override
	public JsonElement put(String key, JsonElement value) {
		return jsonObject.put(key, value);
	}

	@Override
	public JsonElement remove(Object key) {
		return jsonObject.remove(key);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends JsonElement> m) {
		jsonObject.putAll(m);
	}

	@Override
	public void clear() {
		jsonObject.clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return jsonObject.keySet();
	}

	@NotNull
	@Override
	public Collection<JsonElement> values() {
		return jsonObject.values();
	}

	@NotNull
	@Override
	public Set<Entry<String, JsonElement>> entrySet() {
		return jsonObject.entrySet();
	}
}
