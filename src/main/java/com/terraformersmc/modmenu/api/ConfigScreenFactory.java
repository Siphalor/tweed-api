package com.terraformersmc.modmenu.api;

import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@FunctionalInterface
public interface ConfigScreenFactory<S extends Screen> extends Function<Screen, S> {
	S create(Screen parent);

	@Override
	default S apply(Screen parent) {
		return create(parent);
	}
}
