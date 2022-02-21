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

import com.google.common.collect.ImmutableList;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.DataValue;
import de.siphalor.tweed4.data.gson.GsonSerializer;
import de.siphalor.tweed4.data.gson.GsonValue;
import de.siphalor.tweed4.data.hjson.HjsonSerializer;
import de.siphalor.tweed4.data.jankson.JanksonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class DataTests {
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	V read(DataSerializer<V, L, O> serializer, String file) throws IOException {
		try (InputStream is = getClass().getResourceAsStream(file)) {
			return serializer.readValue(is);
		}
	}

	public static <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	O createTestData(DataSerializer<V, L, O> serializer) {
		O object = serializer.newObject();
		object.set("a", true);
		object.set("b", '\n');
		object.set("c", "ü");
		object.set("d", "ÜwÜ");
		object.set("e", Byte.MAX_VALUE);
		object.set("f", Short.MAX_VALUE);
		object.set("g", Integer.MAX_VALUE);
		object.set("h", Long.MAX_VALUE);
		object.set("i", Float.MAX_VALUE);
		object.set("j", Double.MAX_VALUE);
		object.set("k", Double.POSITIVE_INFINITY);
		O other = object.addObject("other");
		other.set("A", "ABC");
		L list = object.addList("list");
		list.set(0, 123);
		list.set(1, "abc");
		//noinspection unchecked
		list.set(2, (V) other);
		return object;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testEqualitySimple() {
		Assertions.assertAll(
				ImmutableList.of(GsonSerializer.INSTANCE, HjsonSerializer.INSTANCE, JanksonSerializer.INSTANCE).stream()
						.map(serializer -> () -> Assertions.assertTrue(
								createTestData((DataSerializer) serializer)
										.equals(createTestData((DataSerializer) serializer)),
								"Failed to compare " + serializer.getId() + " data with itself!"
						))
		);
	}

	@Test
	public void testGsonEqualsHjson() {
		Assertions.assertTrue(
				createTestData(GsonSerializer.INSTANCE).equals(createTestData(HjsonSerializer.INSTANCE)),
				"Gson and Hjson data is not equal!"
		);
	}

	@Test
	public void testHjsonEqualsGson() {
		Assertions.assertTrue(
				createTestData(HjsonSerializer.INSTANCE).equals(createTestData(GsonSerializer.INSTANCE)),
				"Hjson and Gson data is not equal!"
		);
	}

	@Test
	public void testJanksonEqualsHjson() {
		Assertions.assertTrue(
				createTestData(JanksonSerializer.INSTANCE).equals(createTestData(HjsonSerializer.INSTANCE)),
				"Jankson and Hjson data is not equal!"
		);
	}

	@Test
	public void testHjsonEqualsJankson() {
		Assertions.assertTrue(
				createTestData(HjsonSerializer.INSTANCE).equals(createTestData(JanksonSerializer.INSTANCE)),
				"Hjson and Jankson data is not equal!"
		);
	}

	@Test
	public void testGsonEqualsJankson() {
		Assertions.assertTrue(
				createTestData(GsonSerializer.INSTANCE).equals(createTestData(JanksonSerializer.INSTANCE)),
				"Gson and Jankson data is not equal!"
		);
	}

	@Test
	public void testJanksonEqualsGson() {
		Assertions.assertTrue(
				createTestData(JanksonSerializer.INSTANCE).equals(createTestData(GsonSerializer.INSTANCE)),
				"Jankson and Gson data is not equal!"
		);
	}

	@Test
	public void testGsonToHjson() throws IOException {
		GsonValue gsonData = read(GsonSerializer.INSTANCE, "conversion.json");
		Assertions.assertTrue(
				read(HjsonSerializer.INSTANCE, "conversion.hjson").equals(gsonData.convert(HjsonSerializer.INSTANCE))
		);
	}

	@Test
	public void testGsonToJankson() throws IOException {
		GsonValue gsonData = read(GsonSerializer.INSTANCE, "conversion.json");
		Assertions.assertTrue(
				read(JanksonSerializer.INSTANCE, "conversion.json5").equals(gsonData.convert(JanksonSerializer.INSTANCE))
		);
	}
}
