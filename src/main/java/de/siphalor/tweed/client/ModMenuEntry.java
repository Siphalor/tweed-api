package de.siphalor.tweed.client;

import com.terraformersmc.modmenu.api.ModMenuApi;
import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.tailor.ClothTailor;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModMenuEntry implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ClothTailor.INSTANCE.getScreenFactories().getOrDefault(Tweed.MOD_ID, parent -> null);
	}

	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ClothTailor.INSTANCE.getScreenFactories();
	}
}
