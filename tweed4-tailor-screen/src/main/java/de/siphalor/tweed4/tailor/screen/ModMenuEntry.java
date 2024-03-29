/*
 * Copyright 2021-2022 Siphalor
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
