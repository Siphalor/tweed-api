package de.siphalor.tweed.config.annotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this in {@link AConfigEntry} annotations to define constraints for the entry.
 *
 * @see de.siphalor.tweed.config.constraints.Constraint
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AConfigConstraint {
	/**
	 * @return A class that implements {@link de.siphalor.tweed.config.constraints.Constraint}.
	 */
	Class<?> value();

	/**
	 * @return The parameter string to pass to the constraint.
	 * See the documentation on the specific {@link de.siphalor.tweed.config.constraints.Constraint} for more information.
	 */
	String param() default "";
}
