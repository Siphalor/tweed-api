package de.siphalor.tweedtest;

import com.google.common.collect.ImmutableMap;
import de.siphalor.tweed.tailor.DropdownMaterial;

import java.util.Collection;
import java.util.Map;

public class TestDropdown implements DropdownMaterial<TestDropdown> {
	public static final TestDropdown A = new TestDropdown("a");
	public static final TestDropdown B = new TestDropdown("b");
	public static final TestDropdown C = new TestDropdown("c");

	private static final Map<String, TestDropdown> values = ImmutableMap.of(
			"a", A,
			"b", B,
			"c", C
	);

	private final String name;

	private TestDropdown(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<TestDropdown> values() {
		return values.values();
	}

	@Override
	public String getTranslationKey() {
		return "tweedtest.dropdown." + name;
	}

	@Override
	public DropdownMaterial<TestDropdown> valueOf(String name) {
		return values.get(name);
	}
}
