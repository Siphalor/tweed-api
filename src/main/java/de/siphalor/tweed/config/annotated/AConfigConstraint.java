package de.siphalor.tweed.config.annotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AConfigConstraint {
	Class<?> value();
	String param() default "";
}
