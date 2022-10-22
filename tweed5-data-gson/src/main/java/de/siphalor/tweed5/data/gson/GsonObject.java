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

package de.siphalor.tweed5.data.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.siphalor.tweed5.data.DataObject;
import de.siphalor.tweed5.data.DataSerializer;
import de.siphalor.tweed5.data.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GsonObject implements DataObject<JsonElement> {
	private final JsonObject jsonObject;

	public GsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public GsonObject() {
		this(new JsonObject());
	}

	@Override
	public @NotNull JsonElement getValue() {
		return jsonObject;
	}

	@Override
	public String getComment(String key) {
		return null;
	}

	@Override
	public void setComment(String key, String comment) {

	}

	@Override
	public DataSerializer<JsonElement> getSerializer() {
		return GsonSerializer.INSTANCE;
	}

	@Override
	public int size() {
		return jsonObject.size();
	}

	@Override
	public boolean isEmpty() {
		return jsonObject.size() == 0;
	}

	@Override
	public boolean has(String key) {
		return jsonObject.has(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getValue().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public JsonElement get(Object key) {
		return jsonObject.get((String) key);
	}

	@Nullable
	@Override
	public JsonElement put(String key, JsonElement value) {
		JsonElement old = jsonObject.get(key);
		jsonObject.add(key, value);
		return old;
	}

	@Override
	public JsonElement remove(Object key) {
		return jsonObject.remove((String) key);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends JsonElement> m) {
		for (Entry<? extends String, ? extends JsonElement> entry : m.entrySet()) {
			jsonObject.add(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		jsonObject.entrySet().clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return CollectionUtils.mapSet(jsonObject.entrySet(), Entry::getKey);
	}

	@NotNull
	@Override
	public Collection<JsonElement> values() {
		return CollectionUtils.mapCollection(jsonObject.entrySet(), Entry::getValue);
	}

	@NotNull
	@Override
	public Set<Entry<String, JsonElement>> entrySet() {
		return jsonObject.entrySet();
	}
}
