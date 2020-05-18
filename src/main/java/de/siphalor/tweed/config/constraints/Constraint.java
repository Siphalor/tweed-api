package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.ValueConfigEntry;

public interface Constraint<T> {
	/**
	 * A constraint which may modify the {@link ValueConfigEntry} or if an error occurred should throw a {@link ConstraintException}.
	 *
	 * @param value
	 * @param configEntry the entry to check and/or modify
	 * @throws ConstraintException a possible exception in case of problems
	 */
	void apply(T value, ValueConfigEntry<T> configEntry) throws ConstraintException;

	String getDescription();
}
