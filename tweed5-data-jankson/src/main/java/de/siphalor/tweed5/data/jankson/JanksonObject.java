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

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import de.siphalor.tweed5.data.DataObject;
import de.siphalor.tweed5.data.DataSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class JanksonObject implements DataObject<JsonElement> {
	private final JsonObject jsonObject;

	public JanksonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JanksonObject() {
		this(new JsonObject());
	}

	@Override
	public @NotNull JsonElement getValue() {
		return jsonObject;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String getComment(String key) {
		return jsonObject.getComment(key);
	}

	@Override
	public void setComment(String key, String comment) {
		jsonObject.setComment(key, comment);
	}

	@Override
	public boolean has(String key) {
		return jsonObject.containsKey(key);
	}

	@Override
	public DataSerializer<JsonElement> getSerializer() {
		return JanksonSerializer.INSTANCE;
	}

	@Override
	public int size() {
		return jsonObject.size();
	}

	@Override
	public boolean isEmpty() {
		return jsonObject.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return jsonObject.containsValue(value);
	}

	@Override
	public JsonElement get(Object key) {
		return jsonObject.get(key);
	}

	@Nullable
	@Override
	public JsonElement put(String key, JsonElement value) {
		return jsonObject.put(key, value);
	}

	@Override
	public JsonElement remove(Object key) {
		return jsonObject.remove(key);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends JsonElement> m) {
		jsonObject.putAll(m);
	}

	@Override
	public void clear() {
		jsonObject.clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return jsonObject.keySet();
	}

	@NotNull
	@Override
	public Collection<JsonElement> values() {
		return jsonObject.values();
	}

	@NotNull
	@Override
	public Set<Entry<String, JsonElement>> entrySet() {
		return jsonObject.entrySet();
	}
}
