package de.siphalor.tweed4.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this annotation to a config category class to set the background of the entry.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AConfigBackground {
	/**
	 * @return The identifier for the background texture
	 */
	String value();
}
