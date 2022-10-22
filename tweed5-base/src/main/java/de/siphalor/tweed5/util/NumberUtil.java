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

package de.siphalor.tweed5.util;

public class NumberUtil {
	@SuppressWarnings("unchecked")
	public static <T extends Number> T parse(String string, Class<T> clazz) {
		if (clazz == Byte.class || clazz == Byte.TYPE) {
			return (T)(Object) Byte.parseByte(string);
		} else if (clazz == Short.class || clazz == Short.TYPE) {
			return (T)(Object) Short.parseShort(string);
		} else if (clazz == Integer.class || clazz == Integer.TYPE) {
			return (T)(Object) Integer.parseInt(string);
		} else if (clazz == Long.class || clazz == Long.TYPE) {
			return (T)(Object) Long.parseLong(string);
		} else if (clazz == Float.class || clazz == Float.TYPE) {
			return (T)(Object) Float.parseFloat(string);
		} else {
			return (T)(Object) Double.parseDouble(string);
		}
	}
}
