package de.siphalor.tweed.tailor.cloth;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.TweedClientInitializer;
import de.siphalor.tweed.config.TweedRegistry;
import de.siphalor.tweed.tailor.ClothTailor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TweedClothInitializer implements TweedClientInitializer {
	@Override
	public void registerClient() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			Registry.register(TweedRegistry.TAILORS, new Identifier(Tweed.MOD_ID, "cloth"), ClothTailor.INSTANCE);
		}
	}
}
