package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.AbstractValueEntry;
import net.minecraft.util.math.MathHelper;

public class IntRangeConstraint extends AbstractRangeConstraint<Integer> {
	public IntRangeConstraint(Integer min, Integer max) {
		super(min, max);
	}

	@Override
	public void clampValue(AbstractValueEntry<Integer, ?> entry) {
        entry.value = MathHelper.clamp(entry.value, this.min, this.max);
	}
}
