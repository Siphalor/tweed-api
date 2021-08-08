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

import de.siphalor.tweed4.data.DataList;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlList extends XmlContainer<Integer> implements DataList<XmlValue, XmlList, XmlObject> {
	private final List<Element> elements;
	private String childNameDefault;

	public XmlList(Element xmlElement) {
		super(xmlElement);
		NodeList nodes = xmlElement.getChildNodes();
		elements = new ArrayList<>(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				elements.add((Element) node);
			}
		}

		if (xmlElement.hasAttribute("childName")) {
			childNameDefault = xmlElement.getAttribute("childName");
		} else {
			if (!elements.isEmpty()) {
				childNameDefault = elements.get(0).getTagName();
			} else {
				String tagName = xmlElement.getTagName();
				if (tagName.endsWith("s")) {
					childNameDefault = tagName.substring(0, tagName.length() - 1);
				} else {
					childNameDefault = "element";
				}
			}
		}
	}

	@Override
	protected XmlValue createTypedChild(Integer index, String type, String value) {
		Element child = xmlElement.getOwnerDocument().createElement(childNameDefault);
		child.setTextContent(value);
		TypedXmlValue typedXmlValue = new TypedXmlValue(child, type);
		set(index, typedXmlValue);
		return typedXmlValue;
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public XmlValue get(Integer index) {
		return XmlValue.of(elements.get(index));
	}

	@Override
	public XmlValue set(Integer index, XmlValue value) {
		if (index < elements.size()) {
			xmlElement.removeChild(elements.get(index));
			xmlElement.getOwnerDocument().adoptNode(value.xmlElement);
			elements.set(index, value.xmlElement);
		} else {
			for (int i = elements.size(); i < index; i++) {
				Element placeholder = xmlElement.getOwnerDocument().createElement(childNameDefault);
				placeholder.setAttribute("type", null);
				elements.add(placeholder);
				xmlElement.appendChild(placeholder);
			}
			elements.add(value.xmlElement);
			xmlElement.getOwnerDocument().adoptNode(value.xmlElement);
			xmlElement.appendChild(value.xmlElement);
		}
		return value;
	}

	@Override
	public XmlObject addObject(Integer integer) {
		Element child = xmlElement.getOwnerDocument().createElement(childNameDefault);
		elements.add(child);
		xmlElement.appendChild(child);
		return new XmlObject(child);
	}

	@Override
	public XmlList addList(Integer integer) {
		Element child = xmlElement.getOwnerDocument().createElement(childNameDefault);
		elements.add(child);
		xmlElement.appendChild(child);
		return new XmlList(child);
	}

	@Override
	public void remove(Integer index) {
		xmlElement.removeChild(elements.get(index));
		elements.remove((int) index);
	}

	@Override
	public XmlList asList() {
		return this;
	}

	@NotNull
	@Override
	public Iterator<XmlValue> iterator() {
		return elements.stream().map(XmlValue::of).iterator();
	}
}
