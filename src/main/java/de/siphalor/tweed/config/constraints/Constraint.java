package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.ConfigEntry;

public interface Constraint<T> {

	/**
	 * A constraint which may modify the {@link ConfigEntry} or if an error occurred should throw a {@link ConstraintException}.
	 * @param configEntry the entry to check and/or modify
	 * @throws ConstraintException a possible exception in case of problems
	 */
	void apply(ConfigEntry<T> configEntry) throws ConstraintException;
}
