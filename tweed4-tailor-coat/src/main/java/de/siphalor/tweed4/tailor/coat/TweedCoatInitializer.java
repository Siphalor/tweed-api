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

package de.siphalor.tweed4.tailor.coat;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.TweedClientInitializer;
import de.siphalor.tweed4.config.TweedRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TweedCoatInitializer implements TweedClientInitializer {
	@Override
	public void tweedRegisterClient() {
		if (FabricLoader.getInstance().isModLoaded("coat")) {
			Registry.register(TweedRegistry.TAILORS, new Identifier(Tweed.MOD_ID, "coat"), CoatTailor.INSTANCE);
		} else {
			Tweed.LOGGER.warn("[tweed4-tailor-coat] Couldn't find Coat - some config screens might not be present.");
		}
	}
}
