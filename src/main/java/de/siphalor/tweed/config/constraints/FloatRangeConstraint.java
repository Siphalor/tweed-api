package de.siphalor.tweed.config.constraints;

/**
 * @deprecated Use {@link RangeConstraint}s instead
 */
@Deprecated
public class FloatRangeConstraint extends RangeConstraint<Float> {

	public FloatRangeConstraint(Float min, Float max) {
        between(min, max);
	}
}
