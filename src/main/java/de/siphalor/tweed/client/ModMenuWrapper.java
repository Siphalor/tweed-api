package de.siphalor.tweed.client;

import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuWrapper {
	static public void addConfigOverride(String modId, Runnable action) {
		ModMenuApi.addConfigOverride(modId, action);
	}
}
