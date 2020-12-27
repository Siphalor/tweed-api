package de.siphalor.tweed.data.serializer;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed.data.DataList;
import de.siphalor.tweed.data.DataObject;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.JsonHelper;

import java.io.*;
import java.util.Iterator;

public class GsonSerializer implements ConfigDataSerializer<JsonElement> {
	protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	public static final GsonSerializer INSTANCE = new GsonSerializer();

	@Override
	public DataObject<JsonElement> newObject() {
		return new GsonObject(new JsonObject());
	}

	@Override
	public DataObject<JsonElement> read(InputStream inputStream) {
		try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
			JsonObject jsonObject = JsonHelper.deserialize(inputStreamReader);
			return new GsonObject(jsonObject);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void write(OutputStream outputStream, DataObject<JsonElement> dataObject) {
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
		try (JsonWriter jsonWriter = GSON.newJsonWriter(outputStreamWriter)) {
			GSON.toJson(dataObject.getRaw(), jsonWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFileExtension() {
		return "json";
	}

	static class GsonValue implements DataValue<JsonElement> {
		final JsonElement jsonElement;

		GsonValue(JsonElement jsonElement) {
            this.jsonElement = jsonElement;
		}

		@Override
		public void setComment(String comment) {

		}

		@Override
		public String getComment() {
			return "";
		}

		@Override
		public boolean isNumber() {
			return JsonHelper.isNumber(jsonElement);
		}

		@Override
		public boolean isString() {
			return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString();
		}

		@Override
		public boolean isBoolean() {
            return jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isBoolean();
		}

		@Override
		public boolean isObject() {
			return jsonElement.isJsonObject();
		}

		@Override
		public boolean isList() {
			return jsonElement.isJsonArray();
		}

		@Override
		public int asInt() {
			return jsonElement.getAsInt();
		}

		@Override
		public float asFloat() {
			return jsonElement.getAsFloat();
		}

		@Override
		public String asString() {
			return jsonElement.getAsString();
		}

		@Override
		public boolean asBoolean() {
			return jsonElement.getAsBoolean();
		}

		@Override
		public DataObject<JsonElement> asObject() {
			return new GsonObject(jsonElement);
		}

		@Override
		public DataList<JsonElement> asList() {
			return new GsonList(jsonElement);
		}

		@Override
		public JsonElement getRaw() {
			return jsonElement;
		}
	}

	static class GsonObject extends GsonValue implements DataObject<JsonElement> {

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
		public DataValue<JsonElement> set(String key, DataValue<JsonElement> value) {
			jsonElement.getAsJsonObject().add(key, value.getRaw());
			return value;
		}

		@Override
		public DataValue<JsonElement> set(String key, boolean value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonObject().add(key, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataValue<JsonElement> set(String key, String value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonObject().add(key, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataValue<JsonElement> set(String key, float value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonObject().add(key, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataValue<JsonElement> set(String key, int value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonObject().add(key, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataObject<JsonElement> addObject(String key) {
			JsonObject jsonObject = new JsonObject();
			jsonElement.getAsJsonObject().add(key, jsonObject);
			return new GsonObject(jsonObject);
		}

		@Override
		public DataList<JsonElement> addList(String key) {
			JsonArray jsonArray = new JsonArray();
			jsonElement.getAsJsonObject().add(key, jsonArray);
			return new GsonList(jsonArray);
		}

		@Override
		public DataValue<JsonElement> get(String key) {
			return new GsonValue(jsonElement.getAsJsonObject().get(key));
		}

		@Override
		public void remove(String key) {
			jsonElement.getAsJsonObject().remove(key);
		}

		@Override
		public Iterator<Pair<String, DataValue<JsonElement>>> iterator() {
			return jsonElement.getAsJsonObject().entrySet().stream().map(entry -> new Pair<>(entry.getKey(), (DataValue<JsonElement>) new GsonValue(entry.getValue()))).iterator();
		}
	}

	static class GsonList extends GsonValue implements DataList<JsonElement> {
		GsonList(JsonElement jsonElement) {
			super(jsonElement);
		}

		@Override
		public int size() {
			return jsonElement.getAsJsonArray().size();
		}

		@Override
		public DataValue<JsonElement> get(Integer index) {
			return new GsonValue(jsonElement.getAsJsonArray().get(index));
		}

		@Override
		public DataValue<JsonElement> set(Integer index, int value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, float value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, String value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, boolean value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public DataValue<JsonElement> set(Integer index, DataValue<JsonElement> value) {
			jsonElement.getAsJsonArray().set(index, value.getRaw());
			return value;
		}

		@Override
		public DataList<JsonElement> addList(Integer index) {
			JsonArray jsonArray = new JsonArray();
			jsonElement.getAsJsonArray().set(index, jsonArray);
			return new GsonList(jsonArray);
		}

		@Override
		public DataObject<JsonElement> addObject(Integer index) {
			JsonObject jsonObject = new JsonObject();
			jsonElement.getAsJsonArray().set(index, jsonObject);
			return new GsonObject(jsonObject);
		}

		@Override
		public void remove(Integer index) {
			jsonElement.getAsJsonArray().remove(index);
		}

		@Override
		public Iterator<DataValue<JsonElement>> iterator() {
			return new Iterator<DataValue<JsonElement>>() {
				final Iterator<JsonElement> jsonElementIterator = jsonElement.getAsJsonArray().iterator();

				@Override
				public boolean hasNext() {
					return jsonElementIterator.hasNext();
				}

				@Override
				public DataValue<JsonElement> next() {
					return new GsonValue(jsonElementIterator.next());
				}
			};
		}
	}
}
