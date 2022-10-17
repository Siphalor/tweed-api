package de.siphalor.tweed4.data.xml.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.function.Supplier;

public class SimpleXmlValue implements XmlValue {
	private final Element element;

	public SimpleXmlValue(Element element) {
		this.element = element;
	}

	@Override
	public @NotNull String getText() {
		return element.getTextContent();
	}

	@Override
	public @Nullable String getType() {
		return element.getAttribute("type");
	}

	@Override
	public @NotNull Element getElement(Supplier<Element> elementSupplier) {
		return element;
	}

	@Override
	public boolean equals(Object obj) {
		return defaultEquals(obj);
	}
}
