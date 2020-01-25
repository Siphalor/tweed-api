package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.ValueEntry;
import de.siphalor.tweed.data.DataValue;

public class RangeConstraint<T extends Number> implements Constraint<T> {

	protected T min;
	protected T max;

	public RangeConstraint() {
		super();
	}

	public RangeConstraint<T> between(T min, T max) {
		this.min = min;
		this.max = max;
		return this;
	}

	public RangeConstraint<T> greaterThan(T min) {
		this.min = min;
		this.max = null;
		return this;
	}

	public RangeConstraint<T> smallerThan(T max) {
		this.min = null;
		this.max = max;
		return this;
	}

	public RangeConstraint<T> everything() {
		this.min = null;
		this.max = null;
		return this;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	public boolean hasRealBounds() {
		return min != null && max != null;
	}

	@Override
	public void apply(DataValue<?> dataValue, ValueEntry<T, ?> configEntry) throws ConstraintException {
        if(min != null && configEntry.getValue().doubleValue() < min.doubleValue()) {
        	configEntry.setValue(min);
        	throw new ConstraintException(configEntry.getValue() + " is smaller than " + min, false);
		}
        if(max != null && configEntry.getValue().doubleValue() > max.doubleValue()) {
        	configEntry.setValue(max);
        	throw new ConstraintException(configEntry.getValue() + " is greater than" + max, false);
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

	public T clampValue(T value) {
		if(value.doubleValue() > min.doubleValue())
			return value.doubleValue() > max.doubleValue() ? max : value;
		else
			return min;
	}
}
