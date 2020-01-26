package de.siphalor.tweed;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class MMT implements ModMenuApi {
	@Override
	public String getModId() {
		return "tweed";
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> Tweed.tweedClothBridge.open();
	}
}
