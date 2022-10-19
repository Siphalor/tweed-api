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

package de.siphalor.tweed4.data.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataSerializer;

import java.util.AbstractList;

public class GsonList extends AbstractList<JsonElement> implements DataList<JsonElement> {
	private final JsonArray jsonArray;

	public GsonList(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public GsonList() {
		this(new JsonArray());
	}

	@Override
	public JsonElement getValue() {
		return jsonArray;
	}

	@Override
	public int size() {
		return jsonArray.size();
	}

	@Override
	public JsonElement get(int index) {
		return jsonArray.get(index);
	}

	@Override
	public JsonElement set(int index, JsonElement element) {
		return super.set(index, element);
	}

	@Override
	public void add(int index, JsonElement element) {
		super.add(index, element);
	}

	@Override
	public JsonElement remove(int index) {
		return super.remove(index);
	}

	@Override
	public String getComment(int index) {
		return null;
	}

	@Override
	public void setComment(int index, String comment) {

	}

	@Override
	public DataSerializer<JsonElement> getSerializer() {
		return GsonSerializer.INSTANCE;
	}

}
