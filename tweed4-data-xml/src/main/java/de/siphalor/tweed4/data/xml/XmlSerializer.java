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

import de.siphalor.tweed4.data.AnnotatedDataValue;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.DataType;
import de.siphalor.tweed4.data.xml.value.DefinedXmlValue;
import de.siphalor.tweed4.data.xml.value.SimpleXmlValue;
import de.siphalor.tweed4.data.xml.value.XmlValue;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XmlSerializer implements DataSerializer<XmlValue> {
	private static final DocumentBuilder DOCUMENT_BUILDER;
	public static final XmlSerializer INSTANCE = new XmlSerializer();

	static {
		try {
			DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public AnnotatedDataValue<XmlValue> read(InputStream inputStream) {
		try {
			Document document = DOCUMENT_BUILDER.parse(inputStream);
			Element documentElement = document.getDocumentElement();
			Node previousSibling = documentElement.getPreviousSibling();
			String comment = null;
			if (previousSibling instanceof Comment) {
				comment = previousSibling.getTextContent();
			}
			return AnnotatedDataValue.of(new SimpleXmlValue(documentElement), comment);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void write(OutputStream outputStream, AnnotatedDataValue<XmlValue> dataValue) {
		Element documentElement = dataValue.getValue().getElement(() -> DOCUMENT_BUILDER.newDocument().createElement("root"));
		Document document = documentElement.getOwnerDocument();
		if (dataValue.getComment() != null) {
			Node previousSibling = documentElement.getPreviousSibling();
			if (previousSibling instanceof Comment) {
				previousSibling.setTextContent(dataValue.getComment());
			} else {
				Comment comment = document.createComment(dataValue.getComment());
				document.insertBefore(comment, documentElement);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(document), new StreamResult(outputStream));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public XmlList newList() {
		return new XmlList(createStandaloneElement());
	}

	@Override
	public XmlObject newObject() {
		return new XmlObject(createStandaloneElement());
	}

	Element createStandaloneElement() {
		Document document = DOCUMENT_BUILDER.newDocument();
		Element root = document.createElement("root");
		document.setXmlStandalone(true);
		document.appendChild(root);
		return root;
	}

	@Override
	public Object toRaw(XmlValue value, @Nullable DataType typeHint) {
		if (value.getType() != null) {
			switch (value.getType()) {
				case "char":
				case "character":
					return value.getText().charAt(0);
				case "string":
				case "text":
					return value.getText();
				case "bool":
				case "boolean":
					return "true".equals(value.getText());
				case "byte":
					return Byte.parseByte(value.getText());
				case "short":
					return Short.parseShort(value.getText());
				case "int":
				case "integer":
					return Integer.parseInt(value.getText());
				case "long":
					return Long.parseLong(value.getText());
				case "float":
					return Float.parseFloat(value.getText());
				case "double":
					return Double.parseDouble(value.getText());
				case "list":
				case "array":
					return new XmlList(value.getElement(this::createStandaloneElement));
				case "object":
				case "map":
					return new XmlObject(value.getElement(this::createStandaloneElement));
				default:
					throw new RuntimeException("Unknown type \"" + value.getType() + "\"");
			}
		}
		if (typeHint != null) {
			try {
				return typeHint.cast(value.getText());
			} catch (IllegalArgumentException ignored) {
			}
		}
		return value.getText();
	}

	@Override
	public XmlValue fromRawPrimitive(Object raw) {
		if (raw instanceof Boolean) {
			return new DefinedXmlValue(raw.toString(), "boolean");
		} else if (raw instanceof Byte) {
			return new DefinedXmlValue(raw.toString(), "byte");
		} else if (raw instanceof Short) {
			return new DefinedXmlValue(raw.toString(), "short");
		} else if (raw instanceof Integer) {
			return new DefinedXmlValue(raw.toString(), "integer");
		} else if (raw instanceof Long) {
			return new DefinedXmlValue(raw.toString(), "long");
		} else if (raw instanceof Float) {
			return new DefinedXmlValue(raw.toString(), "float");
		} else if (raw instanceof Double) {
			return new DefinedXmlValue(raw.toString(), "double");
		} else if (raw instanceof Character) {
			return new DefinedXmlValue(raw.toString(), "character");
		} else if (raw instanceof String) {
			return new DefinedXmlValue(raw.toString(), "string");
		}
		// TODO: Lists and objects and stuff
		throw new RuntimeException("Unknown type \"" + raw.getClass().getName() + "\"");
	}

	@Override
	public String getFileExtension() {
		return "xml";
	}

	@Override
	public String getId() {
		return "tweed4:xml";
	}
}
