/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.data.gson;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import de.siphalor.tweed4.data.serializer.DataSerializer;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Iterator;

public class GsonSerializer implements DataSerializer<GsonSerializer.GsonValue, GsonSerializer.GsonList, GsonSerializer.GsonObject> {
	protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	public static final GsonSerializer INSTANCE = new GsonSerializer();

	@Override
	public GsonObject newObject() {
		return new GsonObject(new JsonObject());
	}

	@Override
	public GsonList newList() {
		return new GsonList(new JsonArray());
	}

	@Override
	public GsonValue newBoolean(boolean value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newChar(char value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newString(String value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newByte(byte value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newShort(short value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newInt(int value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newLong(long value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newFloat(float value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newDouble(double value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonObject read(InputStream inputStream) {
		try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
			JsonObject jsonObject = JsonHelper.deserialize(inputStreamReader);
			return new GsonObject(jsonObject);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void write(OutputStream outputStream, GsonObject dataObject) {
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

	@Override
	public String getId() {
		return "tweed4:gson";
	}

	static class GsonValue implements DataValue<GsonValue, GsonList, GsonObject> {
		protected final JsonElement jsonElement;

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
		public boolean isGenericNumber() {
			return isNumber();
		}

		@Override
		public boolean isNumber() {
			return JsonHelper.isNumber(jsonElement);
		}

		@Override
		public boolean isByte() {
			return isNumber();
		}

		@Override
		public boolean isShort() {
			return isNumber();
		}

		@Override
		public boolean isInt() {
			return isNumber();
		}

		@Override
		public boolean isLong() {
			return isNumber();
		}

		@Override
		public boolean isFloat() {
			return isNumber();
		}

		@Override
		public boolean isDouble() {
			return isNumber();
		}

		@Override
		public boolean isChar() {
			return isString() && asString().length() == 1;
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
		public Number asNumber() {
			return jsonElement.getAsNumber();
		}

		@Override
		public byte asByte() {
			return jsonElement.getAsByte();
		}

		@Override
		public short asShort() {
			return jsonElement.getAsShort();
		}

		@Override
		public int asInt() {
			return jsonElement.getAsInt();
		}

		@Override
		public long asLong() {
			return jsonElement.getAsLong();
		}

		@Override
		public float asFloat() {
			return jsonElement.getAsFloat();
		}

		@Override
		public double asDouble() {
			return jsonElement.getAsDouble();
		}

		@Override
		public char asChar() {
			return jsonElement.getAsCharacter();
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
		public GsonObject asObject() {
			return new GsonObject(jsonElement);
		}

		@Override
		public GsonList asList() {
			return new GsonList(jsonElement);
		}

		@Override
		public JsonElement getRaw() {
			return jsonElement;
		}
	}

	static class GsonObject extends GsonValue implements DataObject<GsonValue, GsonList, GsonObject> {

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

	static class GsonList extends GsonValue implements DataList<GsonValue, GsonList, GsonObject> {
		GsonList(JsonElement jsonElement) {
			super(jsonElement);
		}

		@Override
		public int size() {
			return jsonElement.getAsJsonArray().size();
		}

		@Override
		public GsonValue get(Integer index) {
			return new GsonValue(jsonElement.getAsJsonArray().get(index));
		}

		@Override
		public GsonValue set(Integer index, byte value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, short value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, int value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, long value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, float value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, double value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, char value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, String value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, boolean value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			jsonElement.getAsJsonArray().set(index, jsonPrimitive);
			return new GsonValue(jsonPrimitive);
		}

		@Override
		public GsonValue set(Integer index, GsonValue value) {
			jsonElement.getAsJsonArray().set(index, value.getRaw());
			return value;
		}

		@Override
		public GsonList addList(Integer index) {
			JsonArray jsonArray = new JsonArray();
			jsonElement.getAsJsonArray().set(index, jsonArray);
			return new GsonList(jsonArray);
		}

		@Override
		public GsonObject addObject(Integer index) {
			JsonObject jsonObject = new JsonObject();
			jsonElement.getAsJsonArray().set(index, jsonObject);
			return new GsonObject(jsonObject);
		}

		@Override
		public void remove(Integer index) {
			jsonElement.getAsJsonArray().remove(index);
		}

		@Override
		@NotNull
		public Iterator<GsonValue> iterator() {
			return new Iterator<GsonValue>() {
				final Iterator<JsonElement> jsonElementIterator = jsonElement.getAsJsonArray().iterator();

				@Override
				public boolean hasNext() {
					return jsonElementIterator.hasNext();
				}

				@Override
				public GsonValue next() {
					return new GsonValue(jsonElementIterator.next());
				}
			};
		}
	}
}
