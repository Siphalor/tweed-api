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

package de.siphalor.tweed5.tailor.cloth;

import de.siphalor.tweed5.Tweed;
import de.siphalor.tweed5.TweedClientInitializer;
import de.siphalor.tweed5.TweedRegistries;
import de.siphalor.tweed5.registry.TweedIdentifier;
import net.fabricmc.loader.api.FabricLoader;

public class TweedClothInitializer implements TweedClientInitializer {
	@Override
	public void tweedRegisterClient() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			TweedRegistries.TAILORS.register(TweedIdentifier.of(Tweed.MOD_ID, "cloth"), ClothTailor.INSTANCE);
		} else {
			Tweed.LOGGER.warn("[tweed5-tailor-cloth] Couldn't find Cloth - some config screens might not be present.");
		}
	}
}
