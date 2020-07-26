package de.siphalor.tweed.tailor;

import de.siphalor.tweed.util.StaticStringConvertible;

import java.util.Collection;

public interface DropdownMaterial<T extends DropdownMaterial<T>> extends StaticStringConvertible<DropdownMaterial<T>> {
	String name();
	Collection<T> values();
	default String asString() {
		return name();
	}
}
