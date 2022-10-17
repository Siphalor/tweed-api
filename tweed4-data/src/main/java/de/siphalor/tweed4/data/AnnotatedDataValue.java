package de.siphalor.tweed4.data;

import org.jetbrains.annotations.Nullable;

public class AnnotatedDataValue<V> {
	private final V value;
	private final @Nullable String comment;

	protected AnnotatedDataValue(V value, @Nullable String comment) {
		this.value = value;
		this.comment = comment;
	}

	public static <V> AnnotatedDataValue<V> of(V value) {
		return new AnnotatedDataValue<>(value, null);
	}

	public static <V> AnnotatedDataValue<V> of(V value, String comment) {
		return new AnnotatedDataValue<>(value, comment);
	}

	public V getValue() {
		return value;
	}

	public @Nullable String getComment() {
		return comment;
	}
}
