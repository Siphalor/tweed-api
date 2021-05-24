package de.siphalor.tweed4.util;

public interface StaticStringConvertible<T extends StaticStringConvertible<T>> {
	T valueOf(String name);
	String asString();
}
