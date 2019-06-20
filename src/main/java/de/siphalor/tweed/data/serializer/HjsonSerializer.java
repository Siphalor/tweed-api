package de.siphalor.tweed.data.serializer;

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed.Core;
import de.siphalor.tweed.data.DataObject;
import de.siphalor.tweed.data.DataList;
import de.siphalor.tweed.data.DataValue;
import org.hjson.*;

import java.io.*;
import java.util.Iterator;

public class HjsonSerializer implements ConfigDataSerializer<JsonValue> {
	public static final HjsonSerializer INSTANCE = new HjsonSerializer();

	private HjsonOptions hjsonOptions = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");

	@Override
	public DataObject<JsonValue> newCompound() {
        return new HjsonObject(new JsonObject());
	}

	@Override
	public DataObject<JsonValue> read(InputStream inputStream) {
		JsonValue json;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			json = JsonValue.readHjson(inputStreamReader);
			inputStreamReader.close();
		} catch (Exception e) {
			Core.LOGGER.error("Couldn't load hjson config file");
            e.printStackTrace();
            return null;
		}
        if(!json.isObject()) {
        	Core.LOGGER.error("Config files should contain an hjson object!");
        	return null;
        }
        return new HjsonObject(json.asObject());
	}

	@Override
	public void write(OutputStream outputStream, DataObject<JsonValue> dataObject) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			dataObject.getRaw().writeTo(outputStreamWriter, hjsonOptions);
			outputStreamWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFileExtension() {
		return "hjson";
	}

	static class HjsonValue implements DataValue<JsonValue> {
		JsonValue jsonValue;

		HjsonValue(JsonValue jsonValue) {
			this.jsonValue = jsonValue;
		}

		@Override
		public void setComment(String comment) {
			jsonValue.setComment(CommentType.BOL, CommentStyle.LINE, comment);
		}

		@Override
		public String getComment() {
			return jsonValue.getBOLComment();
		}

		@Override
		public boolean isNumber() {
			return jsonValue.isNumber();
		}

		@Override
		public boolean isString() {
			return jsonValue.isString();
		}

		@Override
		public boolean isBoolean() {
			return jsonValue.isBoolean();
		}

		@Override
		public boolean isCompound() {
			return jsonValue.isObject();
		}

		@Override
		public boolean isList() {
			return jsonValue.isArray();
		}

		@Override
		public int asInt() {
			return jsonValue.asInt();
		}

		@Override
		public float asFloat() {
			return jsonValue.asFloat();
		}

		@Override
		public String asString() {
			return jsonValue.asString();
		}

		@Override
		public boolean asBoolean() {
			return jsonValue.asBoolean();
		}

		@Override
		public DataObject<JsonValue> asCompound() {
			return new HjsonObject(jsonValue.asObject());
		}

		@Override
		public DataList<JsonValue> asList() {
			return new HjsonList(jsonValue.asArray());
		}

		@Override
		public JsonValue getRaw() {
			return jsonValue;
		}
	}

	static class HjsonObject extends HjsonValue implements DataObject<JsonValue> {
		HjsonObject(JsonValue jsonValue) {
			super(jsonValue);
		}

		@Override
		public boolean has(String key) {
			return jsonValue.asObject().has(key);
		}

		@Override
		public DataValue<JsonValue> get(String key) {
			if(!has(key)) return null;
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, int value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, float value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, String value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, boolean value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, DataValue<JsonValue> value) {
            jsonValue.asObject().set(key, value.getRaw());
            return value;
		}

		@Override
		public DataObject<JsonValue> addCompound(String key) {
            JsonObject jsonObject = new JsonObject();
            jsonValue.asObject().set(key, jsonObject);
			return new HjsonObject(jsonObject);
		}

		@Override
		public DataList<JsonValue> addList(String key) {
			JsonArray jsonArray = new JsonArray();
			jsonValue.asObject().set(key, jsonArray);
			return new HjsonList(jsonArray);
		}

		@Override
		public void remove(String key) {
			jsonValue.asObject().remove(key);
		}

		@Override
		public Iterator<Pair<String, DataValue<JsonValue>>> iterator() {
			return jsonValue.asObject().names().stream().map(name -> new Pair<>(name, (DataValue<JsonValue>) new HjsonValue(jsonValue.asObject().get(name)))).iterator();
		}
	}

	static class HjsonList extends HjsonValue implements DataList<JsonValue> {

		HjsonList(JsonValue jsonValue) {
			super(jsonValue);
		}

		@Override
		public int size() {
			return jsonValue.asArray().size();
		}

		@Override
		public DataValue<JsonValue> get(int index) {
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(int index, int value) {
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(int index, float value) {
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(int index, String value) {
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(int index, boolean value) {
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(int index, DataValue<JsonValue> value) {
			jsonValue.asArray().set(index, value.getRaw());
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public Iterator<DataValue<JsonValue>> iterator() {
			return jsonValue.asArray().values().stream().map(json -> (DataValue<JsonValue>) new HjsonValue(json)).iterator();
		}
	}
}
