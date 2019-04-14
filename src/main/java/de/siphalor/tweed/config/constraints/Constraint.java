package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.AbstractValueEntry;
import org.hjson.JsonValue;

public interface Constraint<T> {

	/**
	 * A constraint which may modify the {@link AbstractValueEntry} or if an error occurred should throw a {@link ConstraintException}.
	 * @param jsonValue the {@link JsonValue} readValue in
	 * @param configEntry the entry to check and/or modify
	 * @throws ConstraintException a possible exception in case of problems
	 */
	void apply(JsonValue jsonValue, AbstractValueEntry<T, ?> configEntry) throws ConstraintException;

	default String getDescription() {
		return "";
	}

	/**
	 * Gets the type of the constraint
	 * @return the type
	 * @see Type
	 */
	Type getConstraintType();

	/**
	 * Types of constraints. This determines the position when the constraint gets applied.
	 *
	 * <i>PRE</i> constraints are applied before the {@link AbstractValueEntry} is readValue.
	 * <i>POST</i> constraints are applied after the {@link AbstractValueEntry} is readValue.
	 */
	enum Type {
		PRE,
		POST
	}
}
