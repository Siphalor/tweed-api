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

package de.siphalor.tweed4.client;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.config.*;
import de.siphalor.tweed4.mixin.MinecraftServerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TweedClient implements ClientModInitializer {
	private static Consumer<ConfigFile> syncRunnable;

	@Deprecated
	public static void setSyncRunnable(Runnable syncRunnable) {
		TweedClient.syncRunnable = file -> syncRunnable.run();
	}

	public static void setSyncRunnable(Consumer<ConfigFile> syncRunnable) {
		TweedClient.syncRunnable = syncRunnable;
	}

	@Override
	public void onInitializeClient() {
        ConfigLoader.initialReload(ConfigEnvironment.UNIVERSAL);

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Tweed.MOD_ID, "assets_listener");
			}

			@Override
			public void apply(ResourceManager resourceManager) {
				try {
					ConfigLoader.loadConfigs(resourceManager, ConfigEnvironment.CLIENT, ConfigScope.SMALLEST);
				} catch (Throwable e) {
					Tweed.LOGGER.error("Tweed failed to load config files");
					e.printStackTrace();
				}
			}
		});
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer ->
				ConfigLoader.loadConfigs(((MinecraftServerAccessor) minecraftServer).getServerResourceManager().getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD)
		);

		ClientPlayNetworking.registerGlobalReceiver(Tweed.CONFIG_SYNC_S2C_PACKET, (client, handler, packetByteBuf, packetSender) -> {
			ConfigOrigin origin = packetByteBuf.readEnumConstant(ConfigOrigin.class);
			String fileName = packetByteBuf.readString();
            for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
            	if(configFile.getName().equals(fileName)) {
					configFile.read(packetByteBuf, ConfigEnvironment.SERVER, ConfigScope.WORLD, origin);

					if (syncRunnable != null) {
						syncRunnable.accept(configFile);
						syncRunnable = null;
					}
					break;
				}
			}
		});
	}

	public static boolean isOnRemoteServer() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.world != null && !client.isIntegratedServerRunning() || client.getServer() != null && client.getServer().isRemote();
	}
}
