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

package de.siphalor.tweed4.annotated;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.TweedInitializer;
import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.config.TweedRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class TweedAnnotatedInitializer implements TweedInitializer {
	@Override
	public void tweedInit() {
		List<EntrypointContainer<Object>> entrypoints = FabricLoader.getInstance().getEntrypointContainers(Tweed.MOD_ID + ":config", Object.class);

		for (EntrypointContainer<Object> entrypoint : entrypoints) {
			try {
				ConfigFile configFile = POJOConverter.toConfigFile(entrypoint.getEntrypoint(), entrypoint.getProvider().getMetadata().getId());
				TweedRegistry.registerConfigFile(configFile);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
}
