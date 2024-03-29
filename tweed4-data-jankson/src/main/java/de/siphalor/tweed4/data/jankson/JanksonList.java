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
import de.siphalor.tweed4.data.DataList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JanksonList extends JanksonValue implements DataList<JanksonValue, JanksonList, JanksonObject> {
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
	public JanksonValue addNull(Integer index) {
		self.add(JsonNull.INSTANCE);
		return createDataValue(JsonNull.INSTANCE, index);
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
		for (int i = 0; i < elements.length; i++) {
			results[i] = createDataValue(elements[i], i);
		}
		return Arrays.stream(results).iterator();
	}

	JanksonValue createDataValue(JsonElement jsonElement, int index) {
		return new JanksonValue(jsonElement, (comment) -> {
		}, () -> self.getComment(index), (clazz) -> self.get(clazz, index));
	}
}
