package de.siphalor.tweed.config.constraints;

/**
 * @deprecated Use {@link RangeConstraint}s instead
 */
@Deprecated
public class IntRangeConstraint extends RangeConstraint<Integer> {
	public IntRangeConstraint(Integer min, Integer max) {
		between(min, max);
	}
}
