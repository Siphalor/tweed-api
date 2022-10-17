package de.siphalor.tweed4.data.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GsonObject implements DataObject<JsonElement, GsonList, GsonObject> {
	private final JsonObject jsonObject;

	public GsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public GsonObject() {
		this(new JsonObject());
	}

	@Override
	public @NotNull JsonElement getValue() {
		return jsonObject;
	}

	@Override
	public String getComment(String key) {
		return null;
	}

	@Override
	public void setComment(String key, String comment) {

	}

	@Override
	public DataSerializer<JsonElement, GsonList, GsonObject> getSerializer() {
		return GsonSerializer.INSTANCE;
	}

	@Override
	public int size() {
		return jsonObject.size();
	}

	@Override
	public boolean isEmpty() {
		return jsonObject.size() == 0;
	}

	@Override
	public boolean has(String key) {
		return jsonObject.has(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getValue().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public JsonElement get(Object key) {
		return jsonObject.get((String) key);
	}

	@Nullable
	@Override
	public JsonElement put(String key, JsonElement value) {
		JsonElement old = jsonObject.get(key);
		jsonObject.add(key, value);
		return old;
	}

	@Override
	public JsonElement remove(Object key) {
		return jsonObject.remove((String) key);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends JsonElement> m) {
		for (Entry<? extends String, ? extends JsonElement> entry : m.entrySet()) {
			jsonObject.add(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		jsonObject.entrySet().clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return CollectionUtils.mapSet(jsonObject.entrySet(), Entry::getKey);
	}

	@NotNull
	@Override
	public Collection<JsonElement> values() {
		return CollectionUtils.mapCollection(jsonObject.entrySet(), Entry::getValue);
	}

	@NotNull
	@Override
	public Set<Entry<String, JsonElement>> entrySet() {
		return jsonObject.entrySet();
	}
}
