package de.siphalor.tweed.util;

public interface StaticStringConvertible<T extends StaticStringConvertible<T>> {
	T valueOf(String name);
	String asString();
}
