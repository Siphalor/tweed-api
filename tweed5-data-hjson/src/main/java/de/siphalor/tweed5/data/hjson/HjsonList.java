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

package de.siphalor.tweed5.data.hjson;

import de.siphalor.tweed5.data.DataList;
import de.siphalor.tweed5.data.DataSerializer;
import org.hjson.JsonArray;
import org.hjson.JsonValue;

import java.util.AbstractList;

public class HjsonList extends AbstractList<JsonValue> implements DataList<JsonValue> {
	private final JsonArray jsonArray;

	public HjsonList(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public HjsonList() {
		this(new JsonArray());
	}

	@Override
	public JsonValue getValue() {
		return jsonArray;
	}

	public JsonArray getJsonArray() {
		return jsonArray;
	}

	@Override
	public String getComment(int index) {
		return jsonArray.get(index).getBOLComment();
	}

	@Override
	public void setComment(int index, String comment) {
		jsonArray.setComment(index, comment);
	}

	@Override
	public DataSerializer<JsonValue> getSerializer() {
		return HjsonSerializer.INSTANCE;
	}

	@Override
	public JsonValue get(int index) {
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
	public JsonValue set(int index, JsonValue element) {
		return jsonArray.set(index, element);
	}

	@Override
	public void add(int index, JsonValue element) {
		jsonArray.add(element);
		for (int i = jsonArray.size() - 1; i > index; i--) {
			jsonArray.set(i, jsonArray.get(i - 1));
		}
		jsonArray.set(index, element);
	}

	@Override
	public JsonValue remove(int index) {
		return jsonArray.remove(index);
	}
}
