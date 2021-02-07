package de.siphalor.tweed.config.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a method that'll get called after loading the config entry named {@link AConfigFixer#value()} from the file.<br />
 * This is usually used to update older config formats to newer ones.<br />
 * The annotated method should take two arguments of type {@link de.siphalor.tweed.data.DataObject}.
 * The first one will be the data object that the entry to fix is immediately located in.
 * The second one will be the main data object and should be adjusted to conform to the new data structure.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AConfigFixer {
	/**
	 * @return The name of the config entry to listen for. If none is set, this will listen to the containing type.
	 */
	String value() default "";
}
