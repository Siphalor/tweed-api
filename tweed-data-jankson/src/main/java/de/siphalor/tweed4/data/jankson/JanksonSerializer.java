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

package de.siphalor.tweed4.data.jankson;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import de.siphalor.tweed4.data.DataSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JanksonSerializer implements DataSerializer<JanksonValue, JanksonList, JanksonObject> {
    public static final JanksonSerializer INSTANCE = new JanksonSerializer();

    static final Consumer<String> SET_COMMENT_VOID = comment -> {};
    static final Supplier<String> GET_COMMENT_VOID = () -> "";
    static final Function<Class<?>, Object> AS_VOID = clazz -> null;

	@Override
	public JanksonObject newObject() {
		return new JanksonObject(new JsonObject(), SET_COMMENT_VOID, GET_COMMENT_VOID, AS_VOID);
	}

	@Override
	public JanksonList newList() {
		return new JanksonList(new JsonArray(), SET_COMMENT_VOID, GET_COMMENT_VOID, AS_VOID);
	}

	@Override
	public JanksonValue newBoolean(boolean value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newChar(char value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newString(String value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newByte(byte value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newShort(short value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newInt(int value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newLong(long value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newFloat(float value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue newDouble(double value) {
		return new JanksonValue(new JsonPrimitive(value));
	}

	@Override
	public JanksonValue readValue(InputStream inputStream) {
		try {
			JsonObject jsonObject = Jankson.builder().build().load(inputStream);
			return new JanksonObject(jsonObject, (comment) -> {}, () -> "", (clazz) -> null);
		} catch (IOException | SyntaxError e) {
			System.err.println("Failed to read jankson:");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void writeValue(OutputStream outputStream, JanksonValue dataValue) {
		try {
			outputStream.write(dataValue.getRaw().toJson(true, true).getBytes());
		} catch (IOException e) {
			System.err.println("Failed to write jankson:");
			e.printStackTrace();
		}
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
