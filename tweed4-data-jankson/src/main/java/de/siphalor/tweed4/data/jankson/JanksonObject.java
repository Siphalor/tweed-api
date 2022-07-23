/*
 * Copyright 2021-2022 Siphalor
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
	public JanksonValue addNull(String key) {
		self.put(key, JsonNull.INSTANCE);
		return createDataValue(JsonNull.INSTANCE, key);
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
