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

package de.siphalor.tweed4.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RegistryTests {
	@Test
	public void testRegister() {
		Registry<String, String> registry = new Registry<>();
		String value1 = "value1";
		String value2 = "value2";

		Assertions.assertEquals(value1, registry.register("key1", value1));
		Assertions.assertEquals(value2, registry.register("key2", value2));
		Assertions.assertThrows(IllegalArgumentException.class, () -> registry.register("key1", "value3"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> registry.register("key3", value1));
	}

	@Test
	public void testGet() {
		Registry<String, String> registry = new Registry<>();
		String value1 = "value1";
		String value2 = "value2";

		registry.register("key1", value1);
		registry.register("key2", value2);

		Assertions.assertEquals(value1, registry.get("key1"));
		Assertions.assertEquals(value2, registry.get("key2"));
		Assertions.assertNull(registry.get("key3"));
	}

	@Test
	public void testContains() {
		Registry<String, String> registry = new Registry<>();
		String value1 = "value1";
		String value2 = "value2";

		registry.register("key1", value1);
		registry.register("key2", value2);

		Assertions.assertTrue(registry.containsKey("key1"));
		Assertions.assertTrue(registry.containsKey("key2"));
		Assertions.assertFalse(registry.containsKey("key3"));
	}

	@Test
	public void testGetEntries() {
		Registry<String, String> registry = new Registry<>();
		String value1 = "value1";
		String value2 = "value2";

		registry.register("key1", value1);
		registry.register("key2", value2);

		Set<Map.Entry<String, String>> entries = registry.getEntries();
		Assertions.assertNotNull(entries);
		Assertions.assertEquals(2, entries.size());
		Assertions.assertTrue(entries.contains(new AbstractMap.SimpleEntry<>("key1", value1)));
		Assertions.assertTrue(entries.contains(new AbstractMap.SimpleEntry<>("key2", value2)));
		Assertions.assertFalse(entries.contains(new AbstractMap.SimpleEntry<>("key3", value1)));
		Assertions.assertThrows(Exception.class, () -> entries.add(new AbstractMap.SimpleEntry<>("key3", value1)));
		Assertions.assertThrows(Exception.class, () -> entries.remove(new AbstractMap.SimpleEntry<>("key3", value1)));
	}

	@Test
	public void testGetKeys() {
		Registry<String, String> registry = new Registry<>();
		String value1 = "value1";
		String value2 = "value2";

		registry.register("key1", value1);
		registry.register("key2", value2);

		Set<String> keys = registry.getKeys();
		Assertions.assertNotNull(keys);
		Assertions.assertEquals(2, keys.size());
		Assertions.assertTrue(keys.contains("key1"));
		Assertions.assertTrue(keys.contains("key2"));
		Assertions.assertFalse(keys.contains("key3"));
		Assertions.assertThrows(Exception.class, () -> keys.add("key3"));
		Assertions.assertThrows(Exception.class, () -> keys.remove("key3"));
	}

	@Test
	public void testGetValues() {
		Registry<String, String> registry = new Registry<>();
		String value1 = "value1";
		String value2 = "value2";

		registry.register("key1", value1);
		registry.register("key2", value2);

		Collection<String> values = registry.getValues();
		Assertions.assertNotNull(values);
		Assertions.assertEquals(2, values.size());
		Assertions.assertTrue(values.contains(value1));
		Assertions.assertTrue(values.contains(value2));
		Assertions.assertFalse(values.contains("value3"));
		Assertions.assertThrows(Exception.class, () -> values.add("value3"));
		Assertions.assertThrows(Exception.class, () -> values.remove("value3"));
	}
}
