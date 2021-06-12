/*
 * Copyright 2021 Siphalor
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

package de.siphalor.tweed4.util;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {
	/**
	 * Gets <b>all</b> declared fields. That means also extended fields from superclasses.
	 * @param clazz The base class
	 * @return An array of all the fields
	 */
	public static Field[] getAllDeclaredFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (clazz.getSuperclass() != Object.class) {
			ArrayUtils.addAll(fields, getAllDeclaredFields(clazz.getSuperclass()));
		}
		return fields;
	}

	/**
	 * Gets <b>all</b> declared methods. That means also extended methods from superclasses.
	 * @param clazz The base class
	 * @return An array of all the methods
	 */
	public static Method[] getAllDeclaredMethods(Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		if (clazz.getSuperclass() != Object.class) {
			ArrayUtils.addAll(methods, clazz.getDeclaredMethods());
		}
		return methods;
	}
}
