package de.siphalor.tweedtest;

import de.siphalor.tweed4.tailor.DropdownMaterial;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class TestDropdown implements DropdownMaterial<TestDropdown> {
	private static final Map<String, TestDropdown> VALUES = new TreeMap<>();

	public static final TestDropdown A = new TestDropdown("a");
	public static final TestDropdown B = new TestDropdown("b");
	public static final TestDropdown C = new TestDropdown("c");
	public static final TestDropdown D = new TestDropdown("d");
	public static final TestDropdown E = new TestDropdown("e");
	public static final TestDropdown F = new TestDropdown("f");
	public static final TestDropdown G = new TestDropdown("g");

	private final String name;

	private TestDropdown(String name) {
		this.name = name;
		VALUES.put(name, this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<TestDropdown> values() {
		return VALUES.values();
	}

	@Override
	public String getTranslationKey() {
		return "tweedtest.dropdown." + name;
	}

	@Override
	public DropdownMaterial<TestDropdown> valueOf(String name) {
		return VALUES.get(name);
	}
}
