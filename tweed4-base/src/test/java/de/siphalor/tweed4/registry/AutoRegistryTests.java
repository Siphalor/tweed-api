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
