package de.siphalor.tweed4.data.jankson;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JanksonObject extends JanksonValue implements DataObject<JanksonValue, JanksonList, JanksonObject> {
	JsonObject self;

	JanksonObject(JsonElement jsonElement, Consumer<String> setComment, Supplier<String> getComment, Function<Class<?>, Object> as) {
		super(jsonElement, setComment, getComment, as);
		self = (JsonObject) element;
	}

	@Override
	public boolean has(String key) {
		return self.containsKey(key);
	}

	@Override
	public int size() {
		return self.size();
	}

	@Override
	public JanksonValue get(String key) {
		return createDataValue(self.get(key), key);
	}

	@Override
	public JanksonValue set(String key, int value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, short value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, byte value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, float value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, long value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, String value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, char value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, double value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, boolean value) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
		self.put(key, jsonPrimitive);
		return createDataValue(jsonPrimitive, key);
	}

	@Override
	public JanksonValue set(String key, JanksonValue value) {
		self.put(key, value.getRaw());
		return createDataValue(value.getRaw(), key);
	}

	@Override
	public JanksonObject addObject(String key) {
		JsonObject jsonObject = new JsonObject();
		self.put(key, jsonObject);
		return createDataValue(jsonObject, key).asObject();
	}

	@Override
	public JanksonList addList(String key) {
		JsonArray jsonArray = new JsonArray();
		self.put(key, jsonArray);
		return createDataValue(jsonArray, key).asList();
	}

	@Override
	public void remove(String key) {
		self.remove(key);
	}

	@Override
	@NotNull
	public Iterator<Pair<String, JanksonValue>> iterator() {
		return self.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), createDataValue(entry.getValue(), entry.getKey()))).iterator();
	}

	JanksonValue createDataValue(JsonElement jsonElement, String key) {
		return new JanksonValue(jsonElement, (comment) -> self.setComment(key, comment), () -> self.getComment(key), (clazz) -> self.get(clazz, key));
	}
}
