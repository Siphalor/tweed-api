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

package de.siphalor.tweed5.data.xml;

import de.siphalor.tweed5.data.DataList;
import de.siphalor.tweed5.data.DataSerializer;
import de.siphalor.tweed5.data.xml.value.SimpleXmlValue;
import de.siphalor.tweed5.data.xml.value.XmlValue;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class XmlList extends AbstractList<XmlValue> implements DataList<XmlValue>, XmlBaseContainer {
	private final Element xmlElement;
	private final List<Element> children;
	private final String childNameDefault;

	public XmlList(Element xmlElement) {
		this.xmlElement = xmlElement;
		NodeList nodes = xmlElement.getChildNodes();
		children = new ArrayList<>(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				children.add((Element) node);
			}
		}

		if (xmlElement.hasAttribute("child-name")) {
			childNameDefault = xmlElement.getAttribute("child-name");
		} else {
			if (!children.isEmpty()) {
				childNameDefault = children.get(0).getTagName();
			} else {
				String tagName = xmlElement.getTagName();
				if (tagName.endsWith("ies")) {
					childNameDefault = tagName.substring(0, tagName.length() - 3) + "y";
				} else if (tagName.endsWith("s")) {
					childNameDefault = tagName.substring(0, tagName.length() - 1);
				} else {
					childNameDefault = "element";
				}
			}
		}
	}

	@Override
	public XmlValue getValue() {
		return new SimpleXmlValue(xmlElement);
	}

	@Override
	public String getComment(int index) {
		return getComment(children.get(index));
	}

	@Override
	public void setComment(int index, String comment) {
		setComment(children.get(index), comment);
	}

	private Supplier<Element> getElementSupplier() {
		return () -> xmlElement.getOwnerDocument().createElement(childNameDefault);
	}

	@Override
	public XmlValue get(int index) {
		return new SimpleXmlValue(children.get(index));
	}

	@Override
	public XmlValue set(int index, XmlValue value) {
		Element element = value.getElement(getElementSupplier());
		Element oldElement = children.get(index);
		children.set(index, element);
		xmlElement.replaceChild(element, oldElement);
		return new SimpleXmlValue(oldElement);
	}

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public void add(int index, XmlValue value) {
		Element element = value.getElement(getElementSupplier());
		xmlElement.insertBefore(element, children.get(index));
		children.add(index, element);
	}

	@Override
	public XmlValue remove(int index) {
		Element element = children.remove(index);
		xmlElement.removeChild(element);
		return new SimpleXmlValue(element);
	}

	@Override
	public DataSerializer<XmlValue> getSerializer() {
		return XmlSerializer.INSTANCE;
	}
}
