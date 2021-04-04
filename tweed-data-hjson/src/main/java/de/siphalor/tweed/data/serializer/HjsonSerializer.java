package de.siphalor.tweed.data.serializer;

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed.data.DataList;
import de.siphalor.tweed.data.DataObject;
import de.siphalor.tweed.data.DataValue;
import org.hjson.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Iterator;

public class HjsonSerializer implements ConfigDataSerializer<JsonValue> {
	public static final HjsonSerializer INSTANCE = new HjsonSerializer();

	private final HjsonOptions hjsonOptions = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");

	@Override
	public DataObject<JsonValue> newObject() {
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
			System.err.println("Couldn't load hjson config file");
            e.printStackTrace();
            return null;
		}
        if(!json.isObject()) {
        	System.err.println("Config files should contain an hjson object!");
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
	public String getId() {
		return "tweed:hjson";
	}

	@Override
	public String getFileExtension() {
		return "hjson";
	}

	static class HjsonValue implements DataValue<JsonValue> {
		protected final JsonValue jsonValue;

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
		public boolean isByte() {
			return jsonValue.isNumber();
		}

		@Override
		public boolean isShort() {
			return jsonValue.isNumber();
		}

		@Override
		public boolean isInt() {
			return jsonValue.isNumber();
		}

		@Override
		public boolean isLong() {
			return jsonValue.isNumber();
		}

		@Override
		public boolean isFloat() {
			return jsonValue.isNumber();
		}

		@Override
		public boolean isDouble() {
			return jsonValue.isNumber();
		}

		@Override
		public boolean isCharacter() {
			return jsonValue.isString() && jsonValue.asString().length() == 1;
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
		public boolean isObject() {
			return jsonValue.isObject();
		}

		@Override
		public boolean isList() {
			return jsonValue.isArray();
		}

		@Override
		public byte asByte() {
			return (byte) jsonValue.asInt();
		}

		@Override
		public short asShort() {
			return (short) jsonValue.asInt();
		}

		@Override
		public int asInt() {
			return jsonValue.asInt();
		}

		@Override
		public long asLong() {
			return jsonValue.asLong();
		}

		@Override
		public float asFloat() {
			return jsonValue.asFloat();
		}

		@Override
		public double asDouble() {
			return jsonValue.asDouble();
		}

		@Override
		public char asCharacter() {
			return jsonValue.asString().charAt(0);
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
		public DataObject<JsonValue> asObject() {
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
		public int size() {
			return jsonValue.asObject().size();
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
		public DataValue<JsonValue> set(String key, short value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, byte value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, float value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, long value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, String value) {
			jsonValue.asObject().set(key, value);
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, char value) {
			jsonValue.asObject().set(key, String.valueOf(value));
			return new HjsonValue(jsonValue.asObject().get(key));
		}

		@Override
		public DataValue<JsonValue> set(String key, double value) {
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
		public DataObject<JsonValue> addObject(String key) {
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
		@NotNull
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
		public DataValue<JsonValue> get(Integer index) {
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, byte value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, short value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, int value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, long value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, float value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, double value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, char value) {
			adjustLength(index);
			jsonValue.asArray().set(index, String.valueOf(value));
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, String value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, boolean value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value);
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataValue<JsonValue> set(Integer index, DataValue<JsonValue> value) {
			adjustLength(index);
			jsonValue.asArray().set(index, value.getRaw());
			return new HjsonValue(jsonValue.asArray().get(index));
		}

		@Override
		public DataObject<JsonValue> addObject(Integer index) {
			adjustLength(index);
			jsonValue.asArray().set(index, new JsonObject());
			return new HjsonObject(jsonValue.asArray().get(index));
		}

		@Override
		public DataList<JsonValue> addList(Integer index) {
			adjustLength(index);
			jsonValue.asArray().set(index, new JsonArray());
			return new HjsonList(jsonValue.asArray().get(index));
		}

		private void adjustLength(int index) {
			JsonArray jsonArray = jsonValue.asArray();
			int length = jsonArray.size();
			for(int i = length; i <= index; i++) {
				jsonArray.add(JsonValue.valueOf(null));
			}
		}

		@Override
		public void remove(Integer index) {
			jsonValue.asArray().remove(index);
		}

		@Override
		@NotNull
		public Iterator<DataValue<JsonValue>> iterator() {
			return jsonValue.asArray().values().stream().map(json -> (DataValue<JsonValue>) new HjsonValue(json)).iterator();
		}
	}
}
