package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.AbstractValueEntry;
import org.hjson.JsonValue;

public abstract class AbstractRangeConstraint<T extends Number> implements Constraint<T> {

	protected T min;
	protected T max;

	public AbstractRangeConstraint(T min, T max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public void apply(JsonValue jsonValue, AbstractValueEntry<T, ?> configEntry) throws ConstraintException {
		if(configEntry.value.doubleValue() > this.max.doubleValue() || configEntry.value.doubleValue() < this.min.doubleValue()) {
			clampValue(configEntry);
			throw new ConstraintException("The value " + configEntry + " is not in the range of " + min + " to " + max, false);
		}
	}

	@Override
	public String getDescription() {
		return "Must be between " + min + " and " + max + ".";
	}

	@Override
	public Type getConstraintType() {
		return Type.POST;
	}

	public abstract void clampValue(AbstractValueEntry<T, ?> entry);
}
