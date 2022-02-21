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
