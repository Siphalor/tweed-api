package de.siphalor.tweedtest;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.client.cloth.IntSliderConverter;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.config.annotated.*;
import de.siphalor.tweed.config.constraints.RangeConstraint;
import de.siphalor.tweed.tailor.ClothData;

@ATweedConfig(scope = ConfigScope.GAME, environment = ConfigEnvironment.UNIVERSAL, tailors = "tweed:cloth", casing = CaseFormat.LOWER_HYPHEN)
@ClothData(modid = "tweedtest")
public class Config {
	@AConfigEntry(name = "bool", comment = "Some kind of Boolean")
	public static Boolean aBoolean = true;

	public static boolean primBool = false;

	@AConfigExclude
	public static String test = "abc";

	@AConfigEntry(constraints = @AConfigConstraint(value = RangeConstraint.class, param = "100..200"))
	public static Integer number = 123;

	// TODO: a RangeConstraint can be added, but it won't do anything except throw an Exception when the range is out of bounds
	@AConfigEntry
	@AConfigConverter(type = IntSliderConverter.class, args = {"-100", "100"})
	public static int slider = 0;

	@AConfigEntry(comment = "This is an object")
	public static A a;

	@AConfigTransitive
	public static Trans trans;

	public static ConfigScope scope = ConfigScope.DEFAULT;

	@AConfigEntry(scope = ConfigScope.SMALLEST, environment = ConfigEnvironment.CLIENT, comment = "This is a client side dropdown!")
	public static TestDropdown dropdown = TestDropdown.B;
	public static TestDropdown dropdown2 = TestDropdown.C;

	public static class A {
		public String name = "Siphalor";
	}

	public static class Trans {
		public String type = "blob";
	}
}
