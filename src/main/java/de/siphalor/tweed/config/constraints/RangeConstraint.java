package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.ValueConfigEntry;
import org.apache.commons.lang3.StringUtils;

public class RangeConstraint<T extends Number> implements AnnotationConstraint<T> {
	private final boolean autoCorrect;
	protected T min;
	protected T max;

	public RangeConstraint() {
		this(false);
	}

	public RangeConstraint(boolean autoCorrect) {
		super();
		this.autoCorrect = autoCorrect;
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
	public void apply(T value, ValueConfigEntry<T> configEntry) throws ConstraintException {
        if(min != null && value.doubleValue() < min.doubleValue()) {
        	if (autoCorrect)
        	    configEntry.setValue(min);
        	throw new ConstraintException(configEntry.getValue() + " is smaller than " + min, !autoCorrect);
		}
        if(max != null && value.doubleValue() > max.doubleValue()) {
	        if (autoCorrect)
        	    configEntry.setValue(max);
        	throw new ConstraintException(configEntry.getValue() + " is greater than" + max, !autoCorrect);
		}
	}

	@Override
	public String getDescription() {
		if (min == null) {
			if (max == null) {
				return "Any numerical value";
			} else {
				return "Must be less or equal to " + max;
			}
		} else {
			if (max == null) {
				return "Must be greater or equal to " + min;
			} else {
				return "Must be inclusively between " + min + " and " + max;
			}
		}
	}

	public T clampValue(T value) {
		if(value.doubleValue() > min.doubleValue())
			return value.doubleValue() > max.doubleValue() ? max : value;
		else
			return min;
	}

	@Override
	public void fromAnnotationParam(String param, Class<?> valueType) {
		String[] parts = StringUtils.splitByWholeSeparator(param, "..", 2);
		if (parts.length == 0) {
			throw new RuntimeException("Invalid value \"" + param + "\" for number range constraint");
		}
		if (parts[0].isEmpty()) {
			if (parts.length < 2 || parts[1].isEmpty()) {
				everything();
			} else {
				smallerThan((T)(Object) Double.parseDouble(parts[1]));
			}
		} else {
			if (parts.length < 2 || parts[1].isEmpty()) {
				greaterThan((T)(Object) Double.parseDouble(parts[0]));
			} else {
				between((T)(Object) Double.parseDouble(parts[0]), (T)(Object) Double.parseDouble(parts[1]));
			}
		}
	}
}
