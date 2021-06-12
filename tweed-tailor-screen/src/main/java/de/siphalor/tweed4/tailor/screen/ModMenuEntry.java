package de.siphalor.tweed4.tailor.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.siphalor.tweed4.config.TweedRegistry;

import java.util.HashMap;
import java.util.Map;

public class ModMenuEntry implements ModMenuApi {
	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();

		TweedRegistry.TAILORS.stream().forEach(tailor -> {
			if (tailor instanceof ScreenTailor) {
				for (Map.Entry<String, ScreenTailorScreenFactory<?>> entry : ((ScreenTailor) tailor).getScreenFactories().entrySet()) {
					factories.put(entry.getKey(), parent -> entry.getValue().create(parent));
				}
			}
		});

		return factories;
	}
}
