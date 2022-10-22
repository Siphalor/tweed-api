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

import de.siphalor.tweed5.data.CollectionUtils;
import de.siphalor.tweed5.data.DataObject;
import de.siphalor.tweed5.data.DataSerializer;
import de.siphalor.tweed5.data.xml.value.SimpleXmlValue;
import de.siphalor.tweed5.data.xml.value.XmlValue;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class XmlObject implements DataObject<XmlValue>, XmlBaseContainer {
	private final Element xmlElement;
	private final HashSet<String> keys;

	public XmlObject(Element xmlElement) {
		this.xmlElement = xmlElement;

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
	public @NotNull XmlValue getValue() {
		return new SimpleXmlValue(xmlElement);
	}

	@Override
	public int size() {
		return keys.size();
	}

	@Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		//noinspection SuspiciousMethodCalls
		return values().contains(value);
	}

	@Override
	public String getComment(String key) {
		NodeList nodes = xmlElement.getElementsByTagName(key);
		if (nodes.getLength() > 0) {
			return getComment((Element) nodes.item(0));
		}
		return null;
	}

	@Override
	public void setComment(String key, String comment) {
		NodeList nodes = xmlElement.getElementsByTagName(key);
		if (nodes.getLength() > 0) {
			setComment((Element) nodes.item(0), comment);
		}
	}

	@Override
	public boolean has(String key) {
		return keys.contains(key);
	}

	@Override
	public DataSerializer<XmlValue> getSerializer() {
		return XmlSerializer.INSTANCE;
	}

	@Override
	public XmlValue remove(Object key) {
		NodeList nodes = xmlElement.getElementsByTagName(key.toString());
		Element removedElement = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node removedNode = xmlElement.removeChild(nodes.item(i));
			if (removedNode instanceof Element) {
				removedElement = (Element) removedNode;
			}
		}
		keys.remove(key.toString());
		if (removedElement != null) {
			return new SimpleXmlValue(removedElement);
		} else {
			return null;
		}
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends XmlValue> m) {
		for (Map.Entry<? extends String, ? extends XmlValue> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		NodeList childNodes = xmlElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			xmlElement.removeChild(node);
		}
		keys.clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return new AbstractSet<String>() {
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					private final Iterator<String> iterator = keys.iterator();
					private String last;
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public String next() {
						return last = iterator.next();
					}

					@Override
					public void remove() {
						iterator.remove();
						XmlObject.this.remove(last);
					}
				};
			}

			@Override
			public int size() {
				return keys.size();
			}
		};
	}

	@NotNull
	@Override
	public Collection<XmlValue> values() {
		ArrayList<String> keyList = new ArrayList<>(keys);

		return new AbstractList<XmlValue>() {
			@Override
			public XmlValue get(int index) {
				return XmlObject.this.get(keyList.get(index));
			}

			@Override
			public int size() {
				return keyList.size();
			}

			@Override
			public XmlValue remove(int index) {
				return XmlObject.this.remove(keyList.get(index));
			}
		};
	}

	@NotNull
	@Override
	public Set<Entry<String, XmlValue>> entrySet() {
		return CollectionUtils.mapSet(keys, key -> new Entry<String, XmlValue>() {
			@Override
			public String getKey() {
				return key;
			}

			@Override
			public XmlValue getValue() {
				return XmlObject.this.get(key);
			}

			@Override
			public XmlValue setValue(XmlValue value) {
				return XmlObject.this.put(key, value);
			}
		}, keys -> {
			for (String key : keys) {
				NodeList elements = xmlElement.getElementsByTagName(key);
				for (int i = 0; i < elements.getLength(); i++) {
					xmlElement.removeChild(elements.item(i));
				}
			}
		});
	}

	@Override
	public XmlValue put(String key, XmlValue value) {
		Element oldElement = null;
		Element element = (Element) xmlElement.getElementsByTagName(key).item(0);
		if (element != null) {
			oldElement = (Element) xmlElement.removeChild(element);
		}

		Document document = xmlElement.getOwnerDocument();
		element = value.getElement(() -> document.createElement(key));
		document.adoptNode(element);
		if (!element.getTagName().equals(key)) {
			element = (Element) document.renameNode(element, null, key);
		}
		xmlElement.appendChild(element);
		keys.add(key);

		if (oldElement != null) {
			return new SimpleXmlValue(oldElement);
		} else {
			return null;
		}
	}

	@Override
	public XmlValue get(Object key) {
		NodeList nodes = xmlElement.getElementsByTagName(key.toString());
		if (nodes.getLength() > 0) {
			return new SimpleXmlValue((Element) nodes.item(0));
		}
		return null;
	}
}
