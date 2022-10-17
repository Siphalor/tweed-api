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
import blue.endless.jankson.api.SyntaxError;
import de.siphalor.tweed4.data.AnnotatedDataValue;
import de.siphalor.tweed4.data.DataNull;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.DataType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JanksonSerializer implements DataSerializer<JsonElement, JanksonList, JanksonObject> {
    public static final JanksonSerializer INSTANCE = new JanksonSerializer();

	@Override
	public AnnotatedDataValue<JsonElement> read(InputStream inputStream) {
		try {
			JsonObject jsonObject = Jankson.builder().build().load(inputStream);
			return AnnotatedDataValue.of(jsonObject);
		} catch (IOException | SyntaxError e) {
			System.err.println("Failed to read jankson:");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void write(OutputStream outputStream, AnnotatedDataValue<JsonElement> dataValue) {
		try {
			outputStream.write(dataValue.getValue().toJson(true, true).getBytes());
		} catch (IOException e) {
			System.err.println("Failed to write jankson:");
			e.printStackTrace();
		}
	}

	@Override
	public JanksonList newList() {
		return new JanksonList();
	}

	@Override
	public JanksonObject newObject() {
		return new JanksonObject();
	}

	@Override
	public Object toRaw(JsonElement value, @Nullable DataType typeHint) {
		if (value instanceof JsonPrimitive) {
			JsonPrimitive jsonPrimitive = (JsonPrimitive) value;
			Object actualValue = ((JsonPrimitive) value).getValue();
			if (typeHint != null && typeHint.isNumber() && actualValue instanceof Number) {
				return typeHint.cast(actualValue);
			}
			return jsonPrimitive.getValue();
		} else if (value instanceof JsonArray) {
			return new JanksonList((JsonArray) value);
		} else if (value instanceof JsonObject) {
			return new JanksonObject((JsonObject) value);
		} else if (value instanceof JsonNull) {
			return DataNull.INSTANCE;
		}
		return null;
	}

	@Override
	public JsonElement fromRawPrimitive(Object raw) {
		if (raw instanceof JsonElement) {
			return (JsonElement) raw;
		} else if (raw instanceof Number) {
			return new JsonPrimitive(raw);
		} else if (raw instanceof String) {
			return new JsonPrimitive(raw);
		} else if (raw instanceof Boolean) {
			return new JsonPrimitive(raw);
		} else if (raw instanceof JanksonList) {
			return ((JanksonList) raw).getJsonArray();
		} else if (raw instanceof JanksonObject) {
			return ((JanksonObject) raw).getJsonObject();
		} else if (raw == DataNull.INSTANCE) {
			return JsonNull.INSTANCE;
		}
		return null;
	}

	@Override
	public String getFileExtension() {
		return "jankson";
	}

	@Override
	public String getId() {
		return "tweed4:jankson";
	}
}
