package de.siphalor.tweed4.data.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class GsonObject extends GsonValue implements DataObject<GsonValue, GsonList, GsonObject> {

	GsonObject(JsonElement jsonElement) {
		super(jsonElement);
	}

	@Override
	public boolean has(String key) {
		return jsonElement.getAsJsonObject().has(key);
	}

	@Override
	public int size() {
		return jsonElement.getAsJsonObject().size();
	}

	@Override
	public GsonValue set(String key, GsonValue value) {
		jsonElement.getAsJsonObject().add(key, value.getRaw());
		return value;
	}

	@Override
	public GsonValue set(String key, boolean value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(String key, String value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(String key, char value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(String key, double value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(String key, float value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(String key, long value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(String key, int value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonPrimitive);
	}

	@Override
	public GsonValue set(String key, short value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonElement);
	}

	@Override
	public GsonValue set(String key, byte value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
		return new GsonValue(jsonElement);
	}

	@Override
	public GsonObject addObject(String key) {
		JsonObject jsonObject = new JsonObject();
		jsonElement.getAsJsonObject().add(key, jsonObject);
		return new GsonObject(jsonObject);
	}

	@Override
	public GsonList addList(String key) {
		JsonArray jsonArray = new JsonArray();
		jsonElement.getAsJsonObject().add(key, jsonArray);
		return new GsonList(jsonArray);
	}

	@Override
	public GsonValue get(String key) {
		return new GsonValue(jsonElement.getAsJsonObject().get(key));
	}

	@Override
	public void remove(String key) {
		jsonElement.getAsJsonObject().remove(key);
	}

	@Override
	@NotNull
	public Iterator<Pair<String, GsonValue>> iterator() {
		return jsonElement.getAsJsonObject().entrySet().stream().map(entry -> new Pair<>(entry.getKey(), new GsonValue(entry.getValue()))).iterator();
	}
}
