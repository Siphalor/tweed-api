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

package de.siphalor.tweed5.data.jankson;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import de.siphalor.tweed5.data.DataList;
import de.siphalor.tweed5.data.DataSerializer;

import java.util.AbstractList;

public class JanksonList extends AbstractList<JsonElement> implements DataList<JsonElement> {
	private final JsonArray jsonArray;

	public JanksonList(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public JanksonList() {
		this(new JsonArray());
	}

	@Override
	public JsonElement getValue() {
		return jsonArray;
	}

	public JsonArray getJsonArray() {
		return jsonArray;
	}

	@Override
	public String getComment(int index) {
		return jsonArray.getComment(index);
	}

	@Override
	public void setComment(int index, String comment) {
		jsonArray.setComment(index, comment);
	}

	@Override
	public DataSerializer<JsonElement> getSerializer() {
		return JanksonSerializer.INSTANCE;
	}

	@Override
	public JsonElement get(int index) {
		return jsonArray.get(index);
	}

	@Override
	public int size() {
		return jsonArray.size();
	}

	@Override
	public boolean isEmpty() {
		return jsonArray.isEmpty();
	}

	@Override
	public JsonElement set(int index, JsonElement element) {
		return jsonArray.set(index, element);
	}

	@Override
	public void add(int index, JsonElement element) {
		jsonArray.add(index, element);
	}

	@Override
	public JsonElement remove(int index) {
		return jsonArray.remove(index);
	}
}
