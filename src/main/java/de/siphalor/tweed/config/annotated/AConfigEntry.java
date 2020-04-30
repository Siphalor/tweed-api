package de.siphalor.tweed.config.annotated;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AConfigEntry {
	String name() default "";
	String comment() default "";
	ConfigScope scope() default ConfigScope.DEFAULT;
	ConfigEnvironment environment() default ConfigEnvironment.DEFAULT;
	AConfigConstraint[] constraints() default {};
}
