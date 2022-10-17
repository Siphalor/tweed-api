package de.siphalor.tweed4.data.xml.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.Objects;
import java.util.function.Supplier;

public interface XmlValue {
	@NotNull String getText();
	@Nullable String getType();

	@NotNull Element getElement(Supplier<Element> elementSupplier);

	default boolean defaultEquals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof XmlValue) {
			return getText().equals(((XmlValue) other).getText()) && Objects.equals(getType(), ((XmlValue) other).getType());
		}
		return false;
	}
}
