package de.siphalor.tweed4.data.hjson;

import de.siphalor.tweed4.data.CollectionUtils;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataSerializer;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class HjsonObject implements DataObject<JsonValue, HjsonList, HjsonObject> {
	private final JsonObject jsonObject;

	public HjsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public HjsonObject() {
		this(new JsonObject());
	}

	@Override
	public @NotNull JsonValue getValue() {
		return jsonObject;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String getComment(String key) {
		return jsonObject.get(key).getBOLComment();
	}

	@Override
	public void setComment(String key, String comment) {
		jsonObject.setComment(key, comment);
	}

	@Override
	public boolean has(String key) {
		return jsonObject.has(key);
	}

	@Override
	public DataSerializer<JsonValue, HjsonList, HjsonObject> getSerializer() {
		return HjsonSerializer.INSTANCE;
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
		return jsonObject.has(((String) value));
	}

	@Override
	public JsonValue get(Object key) {
		return jsonObject.get((String) key);
	}

	@Nullable
	@Override
	public JsonValue put(String key, JsonValue value) {
		return jsonObject.set(key, value);
	}

	@Override
	public JsonValue remove(Object key) {
		return jsonObject.remove((String) key);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends JsonValue> map) {
		map.forEach(jsonObject::set);
	}

	@Override
	public void clear() {
		for (String name : jsonObject.names()) {
			jsonObject.remove(name);
		}
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		Set<String> names = new HashSet<>(jsonObject.names());
		return new AbstractSet<String>() {
			@Override
			public Iterator<String> iterator() {
				return CollectionUtils.mapIterator(names.iterator(), Function.identity(), jsonObject::remove);
			}

			@Override
			public int size() {
				return names.size();
			}

			@Override
			public boolean remove(Object o) {
				jsonObject.remove((String) o);
				return names.remove(o);
			}
		};
	}

	@NotNull
	@Override
	public Collection<JsonValue> values() {
		List<String> names = new ArrayList<>(jsonObject.names());
		return CollectionUtils.mapCollection(names, jsonObject::get, keys -> {
			for (String key : keys) {
				jsonObject.remove(key);
			}
		});
	}

	@NotNull
	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		Set<String> names = new HashSet<>(jsonObject.names());
		return CollectionUtils.mapSet(names, key -> new Entry<String, JsonValue>() {
			@Override
			public String getKey() {
				return key;
			}

			@Override
			public JsonValue getValue() {
				return jsonObject.get(key);
			}

			@Override
			public JsonValue setValue(JsonValue value) {
				return jsonObject.set(key, value);
			}
		}, keys -> {
			for (String key : keys) {
				jsonObject.remove(key);
			}
		});
	}
}
