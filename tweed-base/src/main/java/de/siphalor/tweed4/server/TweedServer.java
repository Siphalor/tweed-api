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

package de.siphalor.tweed4.server;

import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigLoader;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.mixin.MinecraftServerAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TweedServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
        ConfigLoader.initialReload(ConfigEnvironment.SERVER);
		ServerLifecycleEvents.SERVER_STARTED.register(server ->
				ConfigLoader.loadConfigs(((MinecraftServerAccessor) server).getServerResourceManager().getResourceManager(), ConfigEnvironment.SERVER, ConfigScope.WORLD)
		);
	}
}
