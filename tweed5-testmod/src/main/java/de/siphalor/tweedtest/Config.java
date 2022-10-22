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

import com.google.common.base.CaseFormat;
import com.mojang.datafixers.util.Either;
import de.siphalor.tweed5.annotated.*;
import de.siphalor.tweed5.config.ConfigScope;
import de.siphalor.tweed5.config.constraints.RangeConstraint;
import de.siphalor.tweed5.config.value.serializer.ReflectiveNullable;
import de.siphalor.tweed5.tailor.cloth.ClothData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
@ATweedConfig(serializer = "tweed4:hjson", scope = "game", environment = "universal", tailors = {"tweed4:coat", "tweed4:json_schema"}, casing = CaseFormat.LOWER_HYPHEN)
@ClothData(modid = "tweed5_testmod")
public class Config {
	@AConfigEntry(name = "bool", comment = "Some kind of Boolean")
	public static Boolean aBoolean = true;

	@AConfigEntry(environment = "synced", comment = "A synced boolean")
	public static boolean primBool = false;

	@AConfigExclude
	public static String test = "abc";

	@ReflectiveNullable
	public static String nullableString;

	@AConfigEntry(constraints = @AConfigConstraint(value = RangeConstraint.class, param = "100..200"))
	public static Integer number = 123;

	@AConfigEntry(comment = "This is an object.\n" +
			"A description with line\n" +
			"\tbreaks\n" +
			"\tand tabs")
	public static A a;

	public static List<Entry> entries = Arrays.asList(
			new Entry("hi", "ho"),
			new Entry("tweed4:id", "tweed4:type")
	);

	public static List<String> strings = Arrays.asList(
			"Hello",
			"World"
	);

	@AConfigTransitive
	public static Trans trans;

	public static ConfigScope scope = ConfigScope.UNSPECIFIED;

	@AConfigEntry(scope = "smallest", environment = "client", comment = "This is a client side dropdown!")
	public static TestDropdown dropdown = TestDropdown.B;
	public static TestDropdown dropdown2 = TestDropdown.C;

	public static Either<Integer, String> intOrString = Either.left(0);

	TestRecursiveType recursiveType;

	public static Category category;

	@AConfigBackground(value = "minecraft:textures/block/netherrack.png")
	public static class A {
		public String name = "Siphalor";
	}

	public static class Entry {
		public String id;
		public String type;
		public Optional<String> comment = Optional.empty();
		@ReflectiveNullable
		public String description;

		public Entry() {

		}

		public Entry(String id, String type) {
			this.id = id;
			this.type = type;
		}
	}

	public static class Trans {
		public String type = "blob";
	}

	public static class Category {
		public List<Entry> entries = new ArrayList<>();

		@AConfigListener
		public void reload() {
			System.out.println("reload!");
		}
	}
}
