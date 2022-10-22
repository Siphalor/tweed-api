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

package de.siphalor.tweed5.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TweedIdentifierTests {
	@Test
	public void testOf() {
		Assertions.assertDoesNotThrow(() -> TweedIdentifier.of("namespace", "path"));
		Assertions.assertDoesNotThrow(() -> TweedIdentifier.of("12_special/namespace", "12_special/path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.of("", "path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.of("namespace", ""));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.of("Namespace", "path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.of("namespace", "Path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.of("name-space", "path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.of("namespace", "pa-th"));
	}

	@Test
	public void testParseDefault() {
		Assertions.assertEquals(TweedIdentifier.of("namespace", "path"), TweedIdentifier.parse("default", "namespace:path"));
		Assertions.assertEquals(TweedIdentifier.of("default", "path"), TweedIdentifier.parse("default", "path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("default", "namespace:path:"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("default", ":path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("default", "namespace:"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("default", "Namespace:path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("default", "namespace:Path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("default", "name-space:path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("default", "namespace:pa-th"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("def-ault", "path"));
	}

	@Test
	public void testParse() {
		Assertions.assertEquals(TweedIdentifier.of("namespace", "path"), TweedIdentifier.parse("namespace:path"));
		Assertions.assertEquals(TweedIdentifier.of("12_special/namespace", "12_special/path"), TweedIdentifier.parse("12_special/namespace:12_special/path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("namespace:path:"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse(":path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("namespace:"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("Namespace:path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("namespace:Path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("name-space:path"));
		Assertions.assertThrows(IllegalArgumentException.class, () -> TweedIdentifier.parse("namespace:pa-th"));
	}

	@Test
	public void testToString() {
		Assertions.assertEquals("namespace:path", TweedIdentifier.of("namespace", "path").toString());
		Assertions.assertEquals("12_special/namespace:12_special/path", TweedIdentifier.of("12_special/namespace", "12_special/path").toString());
	}

	@Test
	public void testEquals() {
		Assertions.assertEquals(TweedIdentifier.of("namespace", "path"), TweedIdentifier.of("namespace", "path"));
		Assertions.assertNotEquals(TweedIdentifier.of("namespace", "path"), TweedIdentifier.of("namespace", "path2"));
		Assertions.assertNotEquals(TweedIdentifier.of("namespace", "path"), TweedIdentifier.of("namespace2", "path"));
		Assertions.assertNotEquals(TweedIdentifier.of("namespace", "path"), TweedIdentifier.of("namespace2", "path2"));
	}
}
