package de.siphalor.tweed4.tailor.screen;

import de.siphalor.tweed4.tailor.Tailor;

import java.util.Map;

public abstract class ScreenTailor extends Tailor {
	public abstract Map<String, ScreenTailorScreenFactory<?>> getScreenFactories();
}
