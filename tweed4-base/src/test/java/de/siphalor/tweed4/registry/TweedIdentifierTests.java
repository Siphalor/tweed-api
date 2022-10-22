package de.siphalor.tweed4.registry;

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
