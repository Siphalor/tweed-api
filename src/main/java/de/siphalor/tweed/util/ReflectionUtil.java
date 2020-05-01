package de.siphalor.tweed.util;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {
	/**
	 * Gets <b>all</b> declared fields. That means also extended fields from superclasses.
	 * @param clazz The base class
	 * @return An array of all the fields
	 */
	public static Field[] getAllDeclaredFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (clazz.getSuperclass() != Object.class) {
			ArrayUtils.addAll(fields, getAllDeclaredFields(clazz.getSuperclass()));
		}
		return fields;
	}

	/**
	 * Gets <b>all</b> declared methods. That means also extended methods from superclasses.
	 * @param clazz The base class
	 * @return An array of all the methods
	 */
	public static Method[] getAllDeclaredMethods(Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		if (clazz.getSuperclass() != Object.class) {
			ArrayUtils.addAll(methods, clazz.getDeclaredMethods());
		}
		return methods;
	}
}
