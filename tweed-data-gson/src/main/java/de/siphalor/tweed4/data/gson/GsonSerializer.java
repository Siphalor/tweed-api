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

package de.siphalor.tweed4.data.gson;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import de.siphalor.tweed4.data.serializer.DataSerializer;
import net.minecraft.util.JsonHelper;

import java.io.*;

public class GsonSerializer implements DataSerializer<GsonValue, GsonList, GsonObject> {
	protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	public static final GsonSerializer INSTANCE = new GsonSerializer();

	@Override
	public GsonObject newObject() {
		return new GsonObject(new JsonObject());
	}

	@Override
	public GsonList newList() {
		return new GsonList(new JsonArray());
	}

	@Override
	public GsonValue newBoolean(boolean value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newChar(char value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newString(String value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newByte(byte value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newShort(short value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newInt(int value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newLong(long value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newFloat(float value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonValue newDouble(double value) {
		return new GsonValue(new JsonPrimitive(value));
	}

	@Override
	public GsonObject read(InputStream inputStream) {
		try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
			JsonObject jsonObject = JsonHelper.deserialize(inputStreamReader);
			return new GsonObject(jsonObject);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void write(OutputStream outputStream, GsonObject dataObject) {
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
		try (JsonWriter jsonWriter = GSON.newJsonWriter(outputStreamWriter)) {
			GSON.toJson(dataObject.getRaw(), jsonWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFileExtension() {
		return "json";
	}

	@Override
	public String getId() {
		return "tweed4:gson";
	}

}
