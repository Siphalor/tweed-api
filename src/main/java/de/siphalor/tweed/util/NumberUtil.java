package de.siphalor.tweed.util;

public class NumberUtil {
	@SuppressWarnings("unchecked")
	public static <T extends Number> T parse(String string, Class<T> clazz) {
		if (clazz == Byte.class || clazz == Byte.TYPE) {
			return (T)(Object) Byte.parseByte(string);
		} else if (clazz == Short.class || clazz == Short.TYPE) {
			return (T)(Object) Short.parseShort(string);
		} else if (clazz == Integer.class || clazz == Integer.TYPE) {
			return (T)(Object) Integer.parseInt(string);
		} else if (clazz == Long.class || clazz == Long.TYPE) {
			return (T)(Object) Long.parseLong(string);
		} else if (clazz == Float.class || clazz == Float.TYPE) {
			return (T)(Object) Float.parseFloat(string);
		} else {
			return (T)(Object) Double.parseDouble(string);
		}
	}
}
