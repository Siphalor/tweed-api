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

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class XmlObject extends XmlContainer<String> implements DataObject<XmlValue, XmlList, XmlObject> {
	private final HashSet<String> keys;

	public XmlObject(Element xmlElement) {
		super(xmlElement);

		if (!xmlElement.hasAttribute("type")) {
			xmlElement.setAttribute("type", "object");
		}

		keys = new HashSet<>();
		NodeList childNodes = xmlElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node instanceof Element) {
				keys.add(((Element) node).getTagName());
			}
		}
	}

	@Override
	public int size() {
		return keys.size();
	}

	@Override
	public boolean has(String key) {
		return keys.contains(key);
	}

	@Override
	public void remove(String key) {
		NodeList nodes = xmlElement.getElementsByTagName(key);
		for (int i = 0; i < nodes.getLength(); i++) {
			xmlElement.removeChild(nodes.item(i));
		}
		keys.remove(key);
	}

	@Override
	public XmlValue set(String key, XmlValue value) {
		Document document = xmlElement.getOwnerDocument();
		document.adoptNode(value.xmlElement);
		value = XmlValue.of((Element) document.renameNode(value.xmlElement, null, key));
		xmlElement.appendChild(value.xmlElement);
		keys.add(key);
		return value;
	}

	@Override
	protected XmlValue createTypedChild(String key, String type, String value) {
		Element child = xmlElement.getOwnerDocument().createElement(key);
		child.setTextContent(value);
		remove(key);
		xmlElement.appendChild(child);
		keys.add(key);
		return new TypedXmlValue(child, type);
	}

	@Override
	public XmlList addList(String key) {
		remove(key);
		keys.add(key);
		return new XmlList(xmlElement.getOwnerDocument().createElement(key));
	}

	@Override
	public XmlObject addObject(String key) {
		remove(key);
		keys.add(key);
		return new XmlObject(xmlElement.getOwnerDocument().createElement(key));
	}

	@Override
	public XmlValue get(String key) {
		NodeList nodes = xmlElement.getElementsByTagName(key);
		if (nodes.getLength() > 0) {
			return XmlValue.of(((Element) nodes.item(0)));
		}
		return null;
	}

	@Override
	public XmlObject asObject() {
		return this;
	}

	@Override
	public Set<String> keys() {
		return keys;
	}

	@Override
	public @NotNull Iterator<Pair<String, XmlValue>> iterator() {
		return keys.stream().map(key -> Pair.of(key, get(key))).iterator();
	}
}
