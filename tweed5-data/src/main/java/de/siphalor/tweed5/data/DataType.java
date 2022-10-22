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

package de.siphalor.tweed5.data;

public enum DataType {
	BOOLEAN(Boolean.class, false),
	CHARACTER(Character.class, false),
	STRING(String.class, false),
	NUMBER(Number.class, true),
	BYTE(Byte.class, true),
	SHORT(Short.class, true),
	INTEGER(Integer.class, true),
	LONG(Long.class, true),
	FLOAT(Float.class, true),
	DOUBLE(Double.class, true),
	LIST(null, false),
	OBJECT(null, false);

	private final Class<?> type;
	private final boolean isNumber;

	DataType(Class<?> type, boolean isNumber) {
		this.type = type;
		this.isNumber = isNumber;
	}

	public boolean isNumber() {
		return isNumber;
	}

	public Object cast(Object value) throws IllegalArgumentException {
		if (type != null && value != null && type == value.getClass()) {
			return value;
		}

		if (this == STRING) {
			if (value == null) {
				return "<null>";
			}
			return value.toString();
		}

		if (isNumber) {
			if (value == null || value instanceof Number) {
				Number number;
				if (value == null) {
					number = 0;
				} else {
					number = (Number) value;
				}

				switch (this) {
					case NUMBER:
						return number;
					case BYTE:
						return number.byteValue();
					case SHORT:
						return number.shortValue();
					case INTEGER:
						return number.intValue();
					case LONG:
						return number.longValue();
					case FLOAT:
						return number.floatValue();
					case DOUBLE:
						return number.doubleValue();
				}
				throw new RuntimeException("Cannot cast number to " + name());
			}

			if (value instanceof String) {
				switch (this) {
					case BYTE:
						return Byte.parseByte((String) value);
					case SHORT:
						return Short.parseShort((String) value);
					case INTEGER:
						return Integer.parseInt((String) value);
					case LONG:
						return Long.parseLong((String) value);
					case FLOAT:
						return Float.parseFloat((String) value);
					case DOUBLE:
					case NUMBER:
						return Double.parseDouble((String) value);
				}
				throw new RuntimeException("Unknown number type");
			}
			throw new IllegalArgumentException("Cannot cast " + value + " to number");
		}
		throw new IllegalArgumentException("Cannot cast " + value + " to " + name());
	}
}
