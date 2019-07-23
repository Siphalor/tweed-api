package de.siphalor.tweed.data.serializer;

import blue.endless.jankson.*;
import blue.endless.jankson.impl.SyntaxError;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.data.DataList;
import de.siphalor.tweed.data.DataObject;
import de.siphalor.tweed.data.DataValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JanksonSerializer implements ConfigDataSerializer<JsonElement> {
    public static final JanksonSerializer INSTANCE = new JanksonSerializer();

	@Override
	public DataObject<JsonElement> newObject() {
		return new JanksonObject(new JsonObject(), (comment) -> {}, () -> "", (clazz) -> null);
	}

	@Override
	public DataObject<JsonElement> read(InputStream inputStream) {
		try {
			JsonObject jsonObject = Jankson.builder().build().load(inputStream);
            return new JanksonObject(jsonObject, (comment) -> {}, () -> "", (clazz) -> null);
		} catch (IOException | SyntaxError e) {
			Tweed.LOGGER.error("Failed to read jankson config file");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void write(OutputStream outputStream, DataObject<JsonElement> dataObject) {
		try {
			outputStream.write(dataObject.getRaw().toJson(true, true).getBytes());
		} catch (IOException e) {
			Tweed.LOGGER.error("Failed to write jankson config file");
			e.printStackTrace();
		}
	}

	@Override
	public String getFileExtension() {
		return "jankson";
	}

    static class JanksonValue implements DataValue<JsonElement> {
        JsonElement element;
        Consumer<String> setComment;
        Supplier<String> getComment;
        Function<Class, Object> as;

		JanksonValue(JsonElement jsonElement, Consumer<String> setComment, Supplier<String> getComment, Function<Class, Object> as) {
			this.element = jsonElement;
			this.setComment = setComment;
			this.getComment = getComment;
			this.as = as;
		}

		@Override
		public void setComment(String comment) {
            setComment.accept(comment);
		}

		@Override
		public String getComment() {
            return getComment.get();
		}

		@Override
		public boolean isNumber() {
            return element instanceof JsonPrimitive && ((JsonPrimitive) element).getValue() instanceof Number;
		}

		@Override
		public boolean isString() {
			return element instanceof JsonPrimitive && ((JsonPrimitive) element).getValue() instanceof String;
		}

		@Override
		public boolean isBoolean() {
			return element instanceof JsonPrimitive && ((JsonPrimitive) element).getValue() instanceof Boolean;
		}

		@Override
		public boolean isObject() {
			return element instanceof JsonObject;
		}

		@Override
		public boolean isList() {
			return element instanceof JsonArray;
		}

		@Override
		public int asInt() {
			return (int) as.apply(Integer.class);
		}

		@Override
		public float asFloat() {
			return (float) as.apply(Float.class);
		}

		@Override
		public String asString() {
			return (String) as.apply(String.class);
		}

		@Override
		public boolean asBoolean() {
			return (Boolean) as.apply(Boolean.class);
		}

		@Override
		public DataObject<JsonElement> asObject() {
			return new JanksonObject(element, setComment, getComment, as);
		}

		@Override
		public DataList<JsonElement> asList() {
			return new JanksonList(element, setComment, getComment, as);
		}

		@Override
		public JsonElement getRaw() {
            return element;
		}
	}

	static class JanksonObject extends JanksonValue implements DataObject<JsonElement> {
		JsonObject self;

		JanksonObject(JsonElement jsonElement, Consumer<String> setComment, Supplier<String> getComment, Function<Class, Object> as) {
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
		public DataValue<JsonElement> get(String key) {
			return createDataValue(self.get(key), key);
		}

		@Override
		public DataValue<JsonElement> set(String key, int value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.put(key, jsonPrimitive);
			return createDataValue(jsonPrimitive, key);
		}

		@Override
		public DataValue<JsonElement> set(String key, float value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.put(key, jsonPrimitive);
			return createDataValue(jsonPrimitive, key);
		}

		@Override
		public DataValue<JsonElement> set(String key, String value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.put(key, jsonPrimitive);
			return createDataValue(jsonPrimitive, key);
		}

		@Override
		public DataValue<JsonElement> set(String key, boolean value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.put(key, jsonPrimitive);
			return createDataValue(jsonPrimitive, key);
		}

		@Override
		public DataValue<JsonElement> set(String key, DataValue<JsonElement> value) {
			self.put(key, value.getRaw());
			return createDataValue(value.getRaw(), key);
		}

		@Override
		public DataObject<JsonElement> addObject(String key) {
			JsonObject jsonObject = new JsonObject();
			self.put(key, jsonObject);
			return createDataValue(jsonObject, key).asObject();
		}

		@Override
		public DataList<JsonElement> addList(String key) {
			JsonArray jsonArray = new JsonArray();
			self.put(key, jsonArray);
			return createDataValue(jsonArray, key).asList();
		}

		@Override
		public void remove(String key) {
            self.remove(key);
		}

		@Override
		public Iterator<Pair<String, DataValue<JsonElement>>> iterator() {
            return self.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), createDataValue(entry.getValue(), entry.getKey()))).iterator();
		}

		DataValue<JsonElement> createDataValue(JsonElement jsonElement, String key) {
			return new JanksonValue(jsonElement, (comment) -> self.setComment(key, comment), () -> self.getComment(key), (clazz) -> self.get(clazz, key));
		}
	}

	static class JanksonList extends JanksonValue implements DataList<JsonElement> {
		JsonArray self;

		JanksonList(JsonElement jsonElement, Consumer<String> setComment, Supplier<String> getComment, Function<Class, Object> as) {
			super(jsonElement, setComment, getComment, as);
			self = (JsonArray) jsonElement;
		}

		@Override
		public int size() {
            return self.size();
		}

		@Override
		public DataValue<JsonElement> get(Integer index) {
			return createDataValue(self.get(index), index);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, int value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
            self.add(jsonPrimitive);
            return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, float value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, String value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, boolean value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, DataValue<JsonElement> value) {
			self.add(value.getRaw());
			return createDataValue(value.getRaw(), self.size() - 1);
		}

		@Override
		public DataObject<JsonElement> addObject(Integer index) {
			JsonObject jsonObject = new JsonObject();
			self.add(jsonObject);
			return createDataValue(jsonObject, self.size() - 1).asObject();
		}

		@Override
		public DataList<JsonElement> addList(Integer index) {
			JsonArray jsonArray = new JsonArray();
			self.add(jsonArray);
			return createDataValue(jsonArray, self.size() - 1).asList();
		}

		@Override
		public void remove(Integer index) {
			self.remove(self.get(index));
		}

		@Override
		public Iterator<DataValue<JsonElement>> iterator() {
            JsonElement[] elements = self.toArray();
			//noinspection unchecked
			DataValue<JsonElement>[] results = new DataValue[size()];
            for(int i = 0; i < elements.length; i++) {
            	results[i] = createDataValue(elements[i], i);
			}
            return Arrays.stream(results).iterator();
		}

		DataValue<JsonElement> createDataValue(JsonElement jsonElement, int index) {
			return new JanksonValue(jsonElement, (comment) -> {}, () -> self.getComment(index), (clazz) -> self.get(clazz, index));
		}
	}
}
