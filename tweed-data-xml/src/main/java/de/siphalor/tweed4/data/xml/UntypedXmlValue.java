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

package de.siphalor.tweed4.data.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.function.Function;

public class UntypedXmlValue extends XmlValue {
	public UntypedXmlValue(Element xmlElement) {
		super(xmlElement);
	}

	private boolean canParse(String text, Function<String, Object> parseFunc) {
		try {
			parseFunc.apply(text);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean isNumber() {
		return canParse(xmlElement.getNodeValue(), Double::parseDouble)
				|| canParse(xmlElement.getNodeValue(), Long::parseLong);
	}

	@Override
	public boolean isGenericNumber() {
		return isNumber();
	}

	@Override
	public boolean isByte() {
		return canParse(xmlElement.getNodeValue(), Byte::parseByte);
	}

	@Override
	public boolean isShort() {
		return canParse(xmlElement.getNodeValue(), Short::parseShort);
	}

	@Override
	public boolean isInt() {
		return canParse(xmlElement.getNodeValue(), Integer::parseInt);
	}

	@Override
	public boolean isLong() {
		return canParse(xmlElement.getNodeValue(), Long::parseLong);
	}

	@Override
	public boolean isFloat() {
		return canParse(xmlElement.getNodeValue(), Float::parseFloat);
	}

	@Override
	public boolean isDouble() {
		return canParse(xmlElement.getNodeValue(), Double::parseDouble);
	}

	@Override
	public boolean isChar() {
		return xmlElement.getNodeValue().length() == 1;
	}

	@Override
	public boolean isString() {
		return !isNumber() && !isBoolean() && !isChar();
	}

	@Override
	public boolean isBoolean() {
		return "true".equals(xmlElement.getNodeValue()) || "false".equals(xmlElement.getNodeValue());
	}

	@Override
	public boolean isObject() {
		NodeList childNodes = xmlElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isList() {
		NodeList childNodes = xmlElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Number asNumber() {
		if (xmlElement.getNodeValue().contains(".")) {
			return Double.parseDouble(xmlElement.getNodeValue());
		} else {
			return Long.parseLong(xmlElement.getNodeValue());
		}
	}
}
