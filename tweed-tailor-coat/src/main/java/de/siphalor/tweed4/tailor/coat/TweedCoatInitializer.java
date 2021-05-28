package de.siphalor.tweed4.tailor.coat;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.TweedClientInitializer;
import de.siphalor.tweed4.config.TweedRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TweedCoatInitializer implements TweedClientInitializer {
	@Override
	public void tweedRegisterClient() {
		Registry.register(TweedRegistry.TAILORS, new Identifier(Tweed.MOD_ID, "coat"), CoatTailor.INSTANCE);
	}
}
