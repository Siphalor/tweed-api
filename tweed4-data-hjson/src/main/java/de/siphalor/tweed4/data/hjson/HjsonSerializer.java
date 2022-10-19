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

package de.siphalor.tweed4.data.hjson;

import de.siphalor.tweed4.data.AnnotatedDataValue;
import de.siphalor.tweed4.data.DataNull;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.DataType;
import org.hjson.HjsonOptions;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class HjsonSerializer implements DataSerializer<JsonValue> {
	public static final HjsonSerializer INSTANCE = new HjsonSerializer();

	private final HjsonOptions hjsonOptions = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");

	@Override
	public HjsonObject newObject() {
        return new HjsonObject();
	}

	@Override
	public HjsonList newList() {
		return new HjsonList();
	}

	@Override
	public AnnotatedDataValue<JsonValue> read(InputStream inputStream) {
		JsonValue json;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			json = JsonValue.readHjson(inputStreamReader);
			inputStreamReader.close();
			return AnnotatedDataValue.of(json, json.getBOLComment());
		} catch (Exception e) {
			System.err.println("Couldn't load hjson config file");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void write(OutputStream outputStream, AnnotatedDataValue<JsonValue> value) {
		value.getValue().setComment(value.getComment());
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			value.getValue().writeTo(outputStreamWriter, hjsonOptions);
			outputStreamWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object toRaw(JsonValue value, @Nullable DataType typeHint) {
		if (value.isBoolean()) {
			return value.asBoolean();
		} else if (value.isNumber()) {
			if (typeHint != null && typeHint.isNumber()) {
				return typeHint.cast(value.asDouble());
			}
			return value.asDouble();
		} else if (value.isString()) {
			return value.asString();
		} else if (value.isArray()) {
			return new HjsonList((JsonArray) value);
		} else if (value.isObject()) {
			return new HjsonObject((JsonObject) value);
		} else if (value.isNull()) {
			return DataNull.INSTANCE;
		} else {
			return null;
		}
	}

	@Override
	public JsonValue fromRawPrimitive(Object raw) {
		if (raw instanceof Boolean) {
			return JsonValue.valueOf(raw);
		} else if (raw instanceof Number) {
			return JsonValue.valueOf(raw);
		} else if (raw instanceof String) {
			return JsonValue.valueOf((String) raw);
		} else if (raw instanceof HjsonList) {
			return ((HjsonList) raw).getJsonArray();
		} else if (raw instanceof HjsonObject) {
			return ((HjsonObject) raw).getJsonObject();
		} else if (raw instanceof DataNull) {
			return JsonValue.valueOf(null);
		} else {
			return null;
		}
	}

	@Override
	public String getId() {
		return "tweed4:hjson";
	}

	@Override
	public String getFileExtension() {
		return "hjson";
	}
}
