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

public class AutoRegistryTests {
	@Test
	public void testRegister() {
		AutoRegistry<String, String> registry = new AutoRegistry<>(val -> "key:" + val);
		String value1 = "value1";
		String value2 = "value2";

		registry.register(value1);
		registry.register(value2);

		Assertions.assertTrue(registry.containsKey("key:value1"));
		Assertions.assertTrue(registry.containsKey("key:value2"));
		Assertions.assertFalse(registry.containsKey("key:value3"));

		Assertions.assertEquals(value1, registry.get("key:value1"));
		Assertions.assertEquals(value2, registry.get("key:value2"));
		Assertions.assertNull(registry.get("key:value3"));
	}
}
