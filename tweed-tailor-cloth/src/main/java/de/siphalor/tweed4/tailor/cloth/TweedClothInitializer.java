package de.siphalor.tweed4.tailor.cloth;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.TweedClientInitializer;
import de.siphalor.tweed4.config.TweedRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TweedClothInitializer implements TweedClientInitializer {
	@Override
	public void tweedRegisterClient() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			Registry.register(TweedRegistry.TAILORS, new Identifier(Tweed.MOD_ID, "cloth"), ClothTailor.INSTANCE);
		}
	}
}
