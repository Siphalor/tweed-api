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

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class UntypedXmlValue extends XmlValue {
	private final BitSet typeSet;

	public UntypedXmlValue(Element xmlElement) {
		super(xmlElement);

		String value = xmlElement.getTextContent();
		typeSet = new BitSet(12);
		typeSet.set(0, canParse(value, Byte::parseByte));
		typeSet.set(1, canParse(value, Short::parseShort));
		typeSet.set(2, canParse(value, Integer::parseInt));
		typeSet.set(3, canParse(value, Long::parseLong));
		typeSet.set(4, canParse(value, Float::parseFloat));
		typeSet.set(5, canParse(value, Double::parseDouble));
		typeSet.set(6, typeSet.get(3) | typeSet.get(5));
		typeSet.set(7, "true".equals(value) || "false".equals(value));

		Set<String> keys = new HashSet<>();
		boolean doubledKeys = false;
		NodeList nodes = xmlElement.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				if (keys.contains(((Element) node).getTagName())) {
					doubledKeys = true;
					break;
				}
				keys.add(((Element) node).getTagName());
			}
		}
		if (!keys.isEmpty()) {
			if (keys.size() == 1) {
				String singleKey = keys.iterator().next();
				if (singleKey.equals("element")) {
					typeSet.set(8);
				} else {
					String tagName = xmlElement.getTagName();
					if (tagName.endsWith("ies")) {
						typeSet.set(8, singleKey.equals(StringUtils.substring(tagName, 0, -3) + "y"));
					} else if (tagName.endsWith("s")) {
						typeSet.set(8, singleKey.equals(StringUtils.substring(tagName, 0, -1)));
					}
				}
			}
			if (!typeSet.get(8)) {
				typeSet.set(8, doubledKeys);
			}
			typeSet.set(9, !typeSet.get(8));
		}

		if (typeSet.isEmpty()) {
			if (value.length() == 1) {
				typeSet.set(10);
			} else {
				typeSet.set(11);
			}
		}
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
		return typeSet.get(6);
	}

	@Override
	public boolean isGenericNumber() {
		return isNumber();
	}

	@Override
	public boolean isByte() {
		return typeSet.get(0);
	}

	@Override
	public boolean isShort() {
		return typeSet.get(1);
	}

	@Override
	public boolean isInt() {
		return typeSet.get(2);
	}

	@Override
	public boolean isLong() {
		return typeSet.get(3);
	}

	@Override
	public boolean isFloat() {
		return typeSet.get(4);
	}

	@Override
	public boolean isDouble() {
		return typeSet.get(5);
	}

	@Override
	public boolean isChar() {
		return typeSet.get(10);
	}

	@Override
	public boolean isString() {
		return typeSet.get(11);
	}

	@Override
	public boolean isBoolean() {
		return "true".equals(xmlElement.getTextContent()) || "false".equals(xmlElement.getTextContent());
	}

	@Override
	public boolean isObject() {
		return typeSet.get(9);
	}

	@Override
	public boolean isList() {
		return typeSet.get(8);
	}

	@Override
	public Number asNumber() {
		if (xmlElement.getTextContent().contains(".")) {
			return Double.parseDouble(xmlElement.getTextContent());
		} else {
			return Long.parseLong(xmlElement.getTextContent());
		}
	}
}
