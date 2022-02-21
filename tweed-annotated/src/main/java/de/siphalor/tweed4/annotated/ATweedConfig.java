/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.annotated;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a POJO as a Tweed config file.<br />
 * You can let Tweed do the registration by registering the annotated class or a field with that class as an entry point <code>tweed4:config</code> in the <code>fabric.mod.json</code> file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ATweedConfig {
	/**
	 * @return The serializer/file type to use for this config file.
	 * @see de.siphalor.tweed4.config.TweedRegistry#SERIALIZERS
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
	 * @see de.siphalor.tweed4.config.TweedRegistry#TAILORS
	 */
	String[] tailors() default {};
}
