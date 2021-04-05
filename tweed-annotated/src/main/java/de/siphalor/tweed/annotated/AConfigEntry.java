package de.siphalor.tweed.annotated;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to add additional information to config entries.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AConfigEntry {
	/**
	 * @return The name to use in the config file for this entry.
	 */
	String name() default "";

	/**
	 * @return The comment/description that will be printed in the config file beneath this entry, if supported.
	 */
	String comment() default "";

	/**
	 * @return Defines the scope where this entry will get reloaded in.
	 */
	ConfigScope scope() default ConfigScope.DEFAULT;

	/**
	 * @return Defines the environment in which this entry will be available.
	 */
	ConfigEnvironment environment() default ConfigEnvironment.DEFAULT;

	/**
	 * @return Defines constraints for this entry.
	 */
	AConfigConstraint[] constraints() default {};
}
