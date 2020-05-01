package de.siphalor.tweed.client;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.tailor.ClothTailor;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModMenuEntry implements ModMenuApi {
	@Override
	public String getModId() {
		return Tweed.MOD_ID;
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ClothTailor.INSTANCE.getScreenFactories().get(Tweed.MOD_ID);
	}

	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ClothTailor.INSTANCE.getScreenFactories();
	}
}
