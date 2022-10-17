package de.siphalor.tweed4.data.xml.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.function.Supplier;

public class DefinedXmlValue implements XmlValue {
	private final @NotNull String text;
	private final @Nullable String type;

	public DefinedXmlValue(@NotNull String text, @Nullable String type) {
		this.text = text;
		this.type = type;
	}


	@Override
	public @NotNull String getText() {
		return text;
	}

	@Override
	public @Nullable String getType() {
		return type;
	}

	@Override
	public @NotNull Element getElement(Supplier<Element> elementSupplier) {
		Element element = elementSupplier.get();
		element.setTextContent(text);
		if (type != null) {
			element.setAttribute("type", type);
		}
		return element;
	}

	@Override
	public boolean equals(Object obj) {
		return defaultEquals(obj);
	}
}
