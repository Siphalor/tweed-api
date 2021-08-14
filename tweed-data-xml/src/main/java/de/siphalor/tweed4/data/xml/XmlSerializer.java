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

import de.siphalor.tweed4.data.DataSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

public class XmlSerializer implements DataSerializer<XmlValue, XmlList, XmlObject> {
	private static DocumentBuilder DOCUMENT_BUILDER;
	public static final XmlSerializer INSTANCE = new XmlSerializer();

	static {
		try {
			DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public XmlValue readValue(InputStream inputStream) {
		try {
			Document document = DOCUMENT_BUILDER.parse(inputStream);
			return XmlValue.of(document.getDocumentElement());
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void writeValue(OutputStream outputStream, XmlValue dataValue) {
		Document document = dataValue.xmlElement.getOwnerDocument();
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
	public XmlValue newBoolean(boolean value) {
		return new TypedXmlValue(createStandaloneElement(), "bool");
	}

	@Override
	public XmlValue newChar(char value) {
		return new TypedXmlValue(createStandaloneElement(), "char");
	}

	@Override
	public XmlValue newString(String value) {
		return new TypedXmlValue(createStandaloneElement(), "string");
	}

	@Override
	public XmlValue newByte(byte value) {
		return new TypedXmlValue(createStandaloneElement(), "byte");
	}

	@Override
	public XmlValue newShort(short value) {
		return new TypedXmlValue(createStandaloneElement(), "short");
	}

	@Override
	public XmlValue newInt(int value) {
		return new TypedXmlValue(createStandaloneElement(), "int");
	}

	@Override
	public XmlValue newLong(long value) {
		return new TypedXmlValue(createStandaloneElement(), "long");
	}

	@Override
	public XmlValue newFloat(float value) {
		return new TypedXmlValue(createStandaloneElement(), "float");
	}

	@Override
	public XmlValue newDouble(double value) {
		return new TypedXmlValue(createStandaloneElement(), "double");
	}

	@Override
	public XmlObject newObject() {
		return new XmlObject(createStandaloneElement());
	}

	@Override
	public XmlValue newNull() {
		return new TypedXmlValue(createStandaloneElement(), "null");
	}

	private Element createStandaloneElement() {
		Document document = DOCUMENT_BUILDER.newDocument();
		Element root = document.createElement("root");
		document.setXmlStandalone(true);
		document.appendChild(root);
		return root;
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
