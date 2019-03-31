package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.ConfigEntry;

public abstract class AbstractRangeConstraint<T extends Number> implements Constraint<T> {

	protected T min;
	protected T max;

	public AbstractRangeConstraint(T min, T max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public void apply(ConfigEntry<T> configEntry) throws ConstraintException {
		if(configEntry.value.doubleValue() > this.max.doubleValue() || configEntry.value.doubleValue() < this.min.doubleValue()) {
			clampValue(configEntry);
			throw new ConstraintException("The value " + configEntry + " is not in the range of " + min + " to " + max, false);
		}
	}

	public abstract void clampValue(ConfigEntry<T> entry);
}
