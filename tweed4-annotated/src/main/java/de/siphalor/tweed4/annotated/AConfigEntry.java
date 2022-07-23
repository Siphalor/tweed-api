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

import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;

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
