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

package de.siphalor.tweed4.data.hjson;

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataObject;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class HjsonObject extends HjsonValue implements DataObject<HjsonValue, HjsonList, HjsonObject> {
	HjsonObject(JsonValue jsonValue) {
		super(jsonValue);
	}

	@Override
	public boolean has(String key) {
		return jsonValue.asObject().has(key);
	}

	@Override
	public int size() {
		return jsonValue.asObject().size();
	}

	@Override
	public HjsonValue get(String key) {
		if (!has(key)) return null;
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, int value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, short value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, byte value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, float value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, long value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, String value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, char value) {
		jsonValue.asObject().set(key, String.valueOf(value));
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, double value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, boolean value) {
		jsonValue.asObject().set(key, value);
		return new HjsonValue(jsonValue.asObject().get(key));
	}

	@Override
	public HjsonValue set(String key, HjsonValue value) {
		jsonValue.asObject().set(key, value.getRaw());
		return value;
	}

	@Override
	public HjsonObject addObject(String key) {
		JsonObject jsonObject = new JsonObject();
		jsonValue.asObject().set(key, jsonObject);
		return new HjsonObject(jsonObject);
	}

	@Override
	public HjsonList addList(String key) {
		JsonArray jsonArray = new JsonArray();
		jsonValue.asObject().set(key, jsonArray);
		return new HjsonList(jsonArray);
	}

	@Override
	public void remove(String key) {
		jsonValue.asObject().remove(key);
	}

	@Override
	@NotNull
	public Iterator<Pair<String, HjsonValue>> iterator() {
		return jsonValue.asObject().names().stream().map(name -> new Pair<>(name, new HjsonValue(jsonValue.asObject().get(name)))).iterator();
	}
}
