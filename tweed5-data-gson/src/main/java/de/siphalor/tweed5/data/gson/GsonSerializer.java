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

import com.google.gson.*;
import de.siphalor.tweed5.data.AnnotatedDataValue;
import de.siphalor.tweed5.data.DataNull;
import de.siphalor.tweed5.data.DataSerializer;
import de.siphalor.tweed5.data.DataType;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class GsonSerializer implements DataSerializer<JsonElement> {
	protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	public static final GsonSerializer INSTANCE = new GsonSerializer();

	@Override
	public AnnotatedDataValue<JsonElement> read(InputStream inputStream) {
		try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
			JsonElement element = new JsonParser().parse(inputStreamReader);
			return AnnotatedDataValue.of(element);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void write(OutputStream outputStream, AnnotatedDataValue<JsonElement> value) {
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
			GSON.toJson(value.getValue(), outputStreamWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object toRaw(JsonElement value, @Nullable DataType typeHint) {
		if (value instanceof JsonArray) {
			return new GsonList((JsonArray) value);
		} else if (value instanceof JsonObject) {
			return new GsonObject((JsonObject) value);
		} else if (value instanceof JsonPrimitive) {
			JsonPrimitive primitive = (JsonPrimitive) value;
			if (primitive.isBoolean()) {
				return primitive.getAsBoolean();
			} else if (primitive.isNumber()) {
				if (typeHint != null && typeHint.isNumber()) {
					return typeHint.cast(primitive.getAsNumber());
				}
				return primitive.getAsNumber();
			} else if (primitive.isString()) {
				return primitive.getAsString();
			}
		} else if (value instanceof JsonNull) {
			return DataNull.INSTANCE;
		}
		return null;
	}

	@Override
	public JsonElement fromRawPrimitive(Object raw) {
		if (raw instanceof Boolean) {
			return new JsonPrimitive((Boolean) raw);
		} else if (raw instanceof Number) {
			return new JsonPrimitive((Number) raw);
		} else if (raw instanceof String) {
			return new JsonPrimitive((String) raw);
		} else if (raw == DataNull.INSTANCE) {
			return JsonNull.INSTANCE;
		}
		return null;
	}

	@Override
	public GsonObject newObject() {
		return new GsonObject();
	}

	@Override
	public GsonList newList() {
		return new GsonList();
	}

	@Override
	public String getId() {
		return "tweed5:gson";
	}

	@Override
	public String getFileExtension() {
		return "json";
	}
}
