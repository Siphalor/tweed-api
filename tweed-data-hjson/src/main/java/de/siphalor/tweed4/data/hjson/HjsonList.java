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

import de.siphalor.tweed4.data.DataList;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class HjsonList extends HjsonValue implements DataList<HjsonValue, HjsonList, HjsonObject> {

	HjsonList(JsonValue jsonValue) {
		super(jsonValue);
	}

	@Override
	public int size() {
		return jsonValue.asArray().size();
	}

	@Override
	public HjsonValue get(Integer index) {
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, byte value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, short value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, int value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, long value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, float value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, double value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, char value) {
		adjustLength(index);
		jsonValue.asArray().set(index, String.valueOf(value));
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, String value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, boolean value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value);
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonValue set(Integer index, HjsonValue value) {
		adjustLength(index);
		jsonValue.asArray().set(index, value.getRaw());
		return new HjsonValue(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonObject addObject(Integer index) {
		adjustLength(index);
		jsonValue.asArray().set(index, new JsonObject());
		return new HjsonObject(jsonValue.asArray().get(index));
	}

	@Override
	public HjsonList addList(Integer index) {
		adjustLength(index);
		jsonValue.asArray().set(index, new JsonArray());
		return new HjsonList(jsonValue.asArray().get(index));
	}

	private void adjustLength(int index) {
		JsonArray jsonArray = jsonValue.asArray();
		int length = jsonArray.size();
		for (int i = length; i <= index; i++) {
			jsonArray.add(JsonValue.valueOf(null));
		}
	}

	@Override
	public void remove(Integer index) {
		jsonValue.asArray().remove(index);
	}

	@Override
	@NotNull
	public Iterator<HjsonValue> iterator() {
		return jsonValue.asArray().values().stream().map(HjsonValue::new).iterator();
	}
}
