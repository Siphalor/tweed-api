package de.siphalor.tweed.config.annotated;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ATweedConfig {
	String serializer() default "hjson";

	String file() default "";

	ConfigScope scope();
	ConfigEnvironment environment();

	CaseFormat casing() default CaseFormat.LOWER_CAMEL;

	String[] tailors() default {};
}
