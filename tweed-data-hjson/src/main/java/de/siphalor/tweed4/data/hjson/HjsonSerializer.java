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

import de.siphalor.tweed4.data.serializer.DataSerializer;
import org.hjson.*;

import java.io.*;

public class HjsonSerializer implements DataSerializer<HjsonValue, HjsonList, HjsonObject> {
	public static final HjsonSerializer INSTANCE = new HjsonSerializer();

	private final HjsonOptions hjsonOptions = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");

	@Override
	public HjsonObject newObject() {
        return new HjsonObject(new JsonObject());
	}

	@Override
	public HjsonList newList() {
		return new HjsonList(new JsonArray());
	}

	@Override
	public HjsonValue newBoolean(boolean value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newChar(char value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newString(String value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newByte(byte value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newShort(short value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newInt(int value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newLong(long value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newFloat(float value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue newDouble(double value) {
		return new HjsonValue(JsonValue.valueOf(value));
	}

	@Override
	public HjsonValue readValue(InputStream inputStream) {
		JsonValue json;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			json = JsonValue.readHjson(inputStreamReader);
			inputStreamReader.close();
			return new HjsonValue(json);
		} catch (Exception e) {
			System.err.println("Couldn't load hjson config file");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void writeValue(OutputStream outputStream, HjsonValue dataValue) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			dataValue.getRaw().writeTo(outputStreamWriter, hjsonOptions);
			outputStreamWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
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
