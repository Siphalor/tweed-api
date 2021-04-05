package de.siphalor.tweed.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A listener method that gets called when the config entry gets changes/reloaded in the specified scope and environment.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AConfigListener {
	/**
	 * @return The entry name of the entry to listen for. If left empty it'll listen for the containing object.
	 */
	String value() default "";
}
