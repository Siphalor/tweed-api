package de.siphalor.tweed.config.constraints;

import de.siphalor.tweed.config.entry.AbstractValueEntry;
import net.minecraft.util.math.MathHelper;

public class FloatRangeConstraint extends AbstractRangeConstraint<Float> {

	public FloatRangeConstraint(Float min, Float max) {
		super(min, max);
	}

	@Override
	public void clampValue(AbstractValueEntry<Float, ?> entry) {
		entry.value = MathHelper.clamp(entry.value, min, max);
	}
}
