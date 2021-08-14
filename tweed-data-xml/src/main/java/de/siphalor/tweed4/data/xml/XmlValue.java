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

import de.siphalor.tweed4.data.DataValue;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XmlValue implements DataValue<XmlValue, XmlList, XmlObject> {
	protected final Element xmlElement;
	private Comment xmlComment;

	public static XmlValue of(Element xmlElement) {
		if (xmlElement.hasAttribute("type")) {
			return new TypedXmlValue(xmlElement, xmlElement.getAttribute("type"));
		} else {
			return new UntypedXmlValue(xmlElement);
		}
	}

	public XmlValue(Element xmlElement) {
		this.xmlElement = xmlElement;
	}

	private void findComment() {
		if (xmlComment != null) return;
		Node parentNode = xmlElement.getParentNode();

		if (parentNode != null) {
			NodeList childNodes = parentNode.getChildNodes();
			Node lastNode = null;
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				if (node == xmlElement) {
					if (lastNode instanceof Comment) {
						xmlComment = (Comment) lastNode;
					}
				}
				lastNode = node;
			}
		}
	}

	@Override
	public void setComment(String comment) {
		findComment();
		if (xmlComment == null) {
			xmlComment = xmlElement.getOwnerDocument().createComment(comment);
			if (xmlElement.getParentNode() != null) {
				xmlElement.getParentNode().insertBefore(xmlComment, xmlElement);
			}
			return;
		}

		xmlComment.setTextContent(comment);
	}

	@Override
	public String getComment() {
		findComment();
		if (xmlComment == null) return null;
		return xmlComment.getTextContent();
	}

	@Override
	public boolean isNull() {
		return "null".equals(xmlElement.getAttribute("type"));
	}

	@Override
	public char asChar() {
		return xmlElement.getTextContent().charAt(0);
	}

	@Override
	public String asString() {
		return xmlElement.getTextContent();
	}

	@Override
	public boolean asBoolean() {
		return "true".equals(xmlElement.getTextContent());
	}

	@Override
	public byte asByte() {
		return Byte.parseByte(xmlElement.getTextContent());
	}

	@Override
	public short asShort() {
		return Short.parseShort(xmlElement.getTextContent());
	}

	@Override
	public int asInt() {
		return Integer.parseInt(xmlElement.getTextContent());
	}

	@Override
	public long asLong() {
		return Long.parseLong(xmlElement.getTextContent());
	}

	@Override
	public float asFloat() {
		return Float.parseFloat(xmlElement.getTextContent());
	}

	@Override
	public double asDouble() {
		return Double.parseDouble(xmlElement.getTextContent());
	}

	@Override
	public XmlList asList() {
		return new XmlList(xmlElement);
	}

	@Override
	public XmlObject asObject() {
		return new XmlObject(xmlElement);
	}
}
