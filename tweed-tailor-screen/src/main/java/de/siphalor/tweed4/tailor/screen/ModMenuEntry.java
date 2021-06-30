/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.tailor.screen;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.config.TweedRegistry;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

public class ModMenuEntry implements ModMenuApi {
	static {
		TweedRegistry.TAILORS.stream().forEach(tailor -> {
			if (tailor instanceof ScreenTailor) {
				((ScreenTailor) tailor).getScreenFactories().forEach((modid, factory) -> {
					ModMenuApi.addConfigOverride(modid, () ->
							MinecraftClient.getInstance().openScreen(factory.create(MinecraftClient.getInstance().currentScreen)));
				});
			}
		});
	}

	@Override
	public String getModId() {
		return Tweed.MOD_ID;
	}
}
