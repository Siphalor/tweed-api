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

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class GsonObject extends GsonValue implements DataObject<GsonValue, GsonList, GsonObject> {

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
		jsonElement.getAsJsonObject().add(key, jsonPrimitive);
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
	public GsonValue addNull(String key) {
		jsonElement.getAsJsonObject().add(key, JsonNull.INSTANCE);
		return new GsonValue(JsonNull.INSTANCE);
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
