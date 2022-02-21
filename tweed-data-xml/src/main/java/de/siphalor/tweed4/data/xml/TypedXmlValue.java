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

package de.siphalor.tweed4.data.xml;

import org.w3c.dom.Element;

import java.util.function.Function;

public class TypedXmlValue extends XmlValue {
	private final Class<?> type;
	private Function<String, Number> numberParser;

	public TypedXmlValue(Element xmlElement, String type) {
		super(xmlElement);
		xmlElement.setAttribute("type", type);
		switch (type) {
			case "char":
			case "character":
				this.type = Character.class;
				break;
			case "string":
			case "text":
				this.type = String.class;
				break;
			case "bool":
			case "boolean":
				this.type = Boolean.class;
				break;
			case "byte":
				this.type = Byte.class;
				this.numberParser = Byte::parseByte;
				break;
			case "short":
				this.type = Short.class;
				this.numberParser = Short::parseShort;
				break;
			case "int":
			case "integer":
				this.type = Integer.class;
				this.numberParser = Integer::parseInt;
				break;
			case "long":
				this.type = Long.class;
				this.numberParser = Long::parseLong;
				break;
			case "float":
				this.type = Float.class;
				this.numberParser = Float::parseFloat;
				break;
			case "double":
				this.type = Double.class;
				this.numberParser = Double::parseDouble;
				break;
			case "list":
			case "array":
				this.type = XmlList.class;
				break;
			case "object":
			case "map":
				this.type = XmlObject.class;
				break;
			default:
				this.type = null;
		}
	}

	@Override
	public boolean isNumber() {
		return Number.class.isAssignableFrom(type);
	}

	@Override
	public boolean isByte() {
		return type == Byte.class;
	}

	@Override
	public boolean isShort() {
		return type == Short.class;
	}

	@Override
	public boolean isInt() {
		return type == Integer.class;
	}

	@Override
	public boolean isLong() {
		return type == Long.class;
	}

	@Override
	public boolean isFloat() {
		return type == Float.class;
	}

	@Override
	public boolean isDouble() {
		return type == Double.class;
	}

	@Override
	public boolean isChar() {
		return type == Character.class;
	}

	@Override
	public boolean isString() {
		return type == String.class;
	}

	@Override
	public boolean isBoolean() {
		return type == Boolean.class;
	}

	@Override
	public boolean isObject() {
		return type == XmlObject.class;
	}

	@Override
	public boolean isList() {
		return type == XmlList.class;
	}

	@Override
	public Number asNumber() {
		return numberParser.apply(xmlElement.getTextContent());
	}
}
