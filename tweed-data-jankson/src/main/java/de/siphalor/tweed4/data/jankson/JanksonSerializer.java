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

package de.siphalor.tweed4.data.jankson;

import blue.endless.jankson.*;
import blue.endless.jankson.impl.SyntaxError;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import de.siphalor.tweed4.data.serializer.DataSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JanksonSerializer implements DataSerializer<JanksonSerializer.JanksonValue, JanksonSerializer.JanksonList, JanksonSerializer.JanksonObject> {
    public static final JanksonSerializer INSTANCE = new JanksonSerializer();

    private static final Consumer<String> SET_COMMENT_VOID = comment -> {};
    private static final Supplier<String> GET_COMMENT_VOID = () -> "";
    private static final Function<Class<?>, Object> AS_VOID = clazz -> null;

	@Override
	public JanksonObject newObject() {
		return new JanksonObject(new JsonObject(), SET_COMMENT_VOID, GET_COMMENT_VOID, AS_VOID);
	}

	@Override
	public JanksonList newList() {
		return new JanksonList(new JsonArray(), SET_COMMENT_VOID, GET_COMMENT_VOID, AS_VOID);
	}

	@Override
	public JanksonValue newBoolean(boolean value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newChar(char value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newString(String value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newByte(byte value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newShort(short value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newInt(int value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newLong(long value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newFloat(float value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newDouble(double value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonObject read(InputStream inputStream) {
		try {
			JsonObject jsonObject = Jankson.builder().build().load(inputStream);
            return new JanksonObject(jsonObject, (comment) -> {}, () -> "", (clazz) -> null);
		} catch (IOException | SyntaxError e) {
			System.err.println("Failed to read jankson config file");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void write(OutputStream outputStream, JanksonObject dataObject) {
		try {
			outputStream.write(dataObject.getRaw().toJson(true, true).getBytes());
		} catch (IOException e) {
			System.err.println("Failed to write jankson config file");
			e.printStackTrace();
		}
	}

	@Override
	public String getFileExtension() {
		return "jankson";
	}

	@Override
	public String getId() {
		return "tweed4:jankson";
	}

	static class JanksonValue implements DataValue<JanksonValue, JanksonList, JanksonObject> {
        protected final JsonElement element;
        Consumer<String> setComment;
        Supplier<String> getComment;
        Function<Class<?>, Object> as;

        JanksonValue(JsonElement jsonElement) {
        	this(jsonElement, SET_COMMENT_VOID, GET_COMMENT_VOID, AS_VOID);
		}

		JanksonValue(JsonElement jsonElement, Consumer<String> setComment, Supplier<String> getComment, Function<Class<?>, Object> as) {
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
		public boolean isGenericNumber() {
			return isNumber();
		}

		@Override
		public boolean isNumber() {
            return element instanceof JsonPrimitive && ((JsonPrimitive) element).getValue() instanceof Number;
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
			return element instanceof JsonPrimitive && (
					(((JsonPrimitive) element).getValue() instanceof String && ((String) ((JsonPrimitive) element).getValue()).length() == 1)
					|| ((JsonPrimitive) element).getValue() instanceof Character
			);
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
		public Number asNumber() {
			return (Number) as.apply(Double.class);
		}

		@Override
		public byte asByte() {
			return (byte) as.apply(Byte.TYPE);
		}

		@Override
		public short asShort() {
			return (short) as.apply(Short.TYPE);
		}

		@Override
		public int asInt() {
			return (int) as.apply(Integer.TYPE);
		}

		@Override
		public long asLong() {
			return (long) as.apply(Long.TYPE);
		}

		@Override
		public float asFloat() {
			return (float) as.apply(Float.TYPE);
		}

		@Override
		public double asDouble() {
			return (double) as.apply(Double.TYPE);
		}

		@Override
		public char asChar() {
			return (char) as.apply(Character.TYPE);
		}

		@Override
		public String asString() {
			return (String) as.apply(String.class);
		}

		@Override
		public boolean asBoolean() {
			return (Boolean) as.apply(Boolean.TYPE);
		}

		@Override
		public JanksonObject asObject() {
			return new JanksonObject(element, setComment, getComment, as);
		}

		@Override
		public JanksonList asList() {
			return new JanksonList(element, setComment, getComment, as);
		}

		@Override
		public JsonElement getRaw() {
            return element;
		}
	}

	static class JanksonObject extends JanksonValue implements DataObject<JanksonValue, JanksonList, JanksonObject> {
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

	static class JanksonList extends JanksonValue implements DataList<JanksonValue, JanksonList, JanksonObject> {
		JsonArray self;

		JanksonList(JsonElement jsonElement, Consumer<String> setComment, Supplier<String> getComment, Function<Class<?>, Object> as) {
			super(jsonElement, setComment, getComment, as);
			self = (JsonArray) jsonElement;
		}

		@Override
		public int size() {
            return self.size();
		}

		@Override
		public JanksonValue get(Integer index) {
			return createDataValue(self.get(index), index);
		}

		@Override
		public JanksonValue set(Integer index, byte value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, short value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, int value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
            self.add(jsonPrimitive);
            return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, long value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, float value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, double value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, char value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, String value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, boolean value) {
			JsonPrimitive jsonPrimitive = new JsonPrimitive(value);
			self.add(jsonPrimitive);
			return createDataValue(jsonPrimitive, self.size() - 1);
		}

		@Override
		public JanksonValue set(Integer index, JanksonValue value) {
			self.add(value.getRaw());
			return createDataValue(value.getRaw(), self.size() - 1);
		}

		@Override
		public JanksonObject addObject(Integer index) {
			JsonObject jsonObject = new JsonObject();
			self.add(jsonObject);
			return createDataValue(jsonObject, self.size() - 1).asObject();
		}

		@Override
		public JanksonList addList(Integer index) {
			JsonArray jsonArray = new JsonArray();
			self.add(jsonArray);
			return createDataValue(jsonArray, self.size() - 1).asList();
		}

		@Override
		public void remove(Integer index) {
			self.remove(self.get(index));
		}

		@Override
		@NotNull
		public Iterator<JanksonValue> iterator() {
            JsonElement[] elements = self.toArray();
			JanksonValue[] results = new JanksonValue[size()];
            for(int i = 0; i < elements.length; i++) {
            	results[i] = createDataValue(elements[i], i);
			}
            return Arrays.stream(results).iterator();
		}

		JanksonValue createDataValue(JsonElement jsonElement, int index) {
			return new JanksonValue(jsonElement, (comment) -> {}, () -> self.getComment(index), (clazz) -> self.get(clazz, index));
		}
	}
}
