package de.siphalor.tweed4.tailor.screen;

import net.minecraft.client.gui.screen.Screen;

@FunctionalInterface
public interface ScreenTailorScreenFactory<S extends Screen> {
	S create(Screen parent);
}
