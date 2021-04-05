package de.siphalor.tweed.annotated;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a POJO as a Tweed config file.<br />
 * You can let Tweed do the registration by registering the annotated class or a field with that class as an entry point <code>tweed:config</code> in the <code>fabric.mod.json</code> file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ATweedConfig {
	/**
	 * @return The serializer/file type to use for this config file.
	 * @see de.siphalor.tweed.config.TweedRegistry#SERIALIZERS
	 */
	String serializer() default "hjson";

	/**
	 * @return Defines the file name for this config file.
	 */
	String file() default "";

	/**
	 * @return The default scope for entries in this config file.
	 */
	ConfigScope scope();

	/**
	 * @return The default environment for entries in this config file.
	 */
	ConfigEnvironment environment();

	/**
	 * @return Sets the casing that will be applied to all config entries.
	 */
	CaseFormat casing() default CaseFormat.LOWER_CAMEL;

	/**
	 * @return Defines the tailors to apply to this file.
	 * @see de.siphalor.tweed.config.TweedRegistry#TAILORS
	 */
	String[] tailors() default {};
}
