/*
 * Copyright 2021 Siphalor
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
import de.siphalor.tweed4.annotated.*;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.constraints.RangeConstraint;
import de.siphalor.tweed4.tailor.cloth.ClothData;

@ATweedConfig(scope = ConfigScope.GAME, environment = ConfigEnvironment.UNIVERSAL, tailors = "tweed4:coat", casing = CaseFormat.LOWER_HYPHEN)
@ClothData(modid = "tweed4_testmod")
public class Config {
	@AConfigEntry(name = "bool", comment = "Some kind of Boolean")
	public static Boolean aBoolean = true;

	public static boolean primBool = false;

	@AConfigExclude
	public static String test = "abc";

	@AConfigEntry(constraints = @AConfigConstraint(value = RangeConstraint.class, param = "100..200"))
	public static Integer number = 123;

	@AConfigEntry(comment = "This is an object.\n" +
			"A description with line\n" +
			"\tbreaks\n" +
			"\tand tabs")
	public static A a;

	@AConfigTransitive
	public static Trans trans;

	public static ConfigScope scope = ConfigScope.DEFAULT;

	@AConfigEntry(scope = ConfigScope.SMALLEST, environment = ConfigEnvironment.CLIENT, comment = "This is a client side dropdown!")
	public static TestDropdown dropdown = TestDropdown.B;
	public static TestDropdown dropdown2 = TestDropdown.C;

	TestRecursiveType recursiveType;

	@AConfigBackground(value = "minecraft:textures/block/netherrack.png")
	public static class A {
		public String name = "Siphalor";
	}

	public static class Trans {
		public String type = "blob";
	}
}
