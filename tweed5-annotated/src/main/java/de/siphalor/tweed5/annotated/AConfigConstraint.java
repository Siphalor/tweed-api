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

package de.siphalor.tweed5.annotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this in {@link AConfigEntry} annotations to define constraints for the entry.
 *
 * @see de.siphalor.tweed5.config.constraints.Constraint
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AConfigConstraint {
	/**
	 * @return A class that implements {@link de.siphalor.tweed5.config.constraints.Constraint}.
	 */
	Class<?> value();

	/**
	 * @return The parameter string to pass to the constraint.
	 * See the documentation on the specific {@link de.siphalor.tweed5.config.constraints.Constraint} for more information.
	 */
	String param() default "";
}
