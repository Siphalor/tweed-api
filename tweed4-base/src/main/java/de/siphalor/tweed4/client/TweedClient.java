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
	private static ConfigSyncListener configSyncListener;

	@Deprecated
	public static void setSyncRunnable(Runnable syncRunnable) {
		setSyncRunnable(file -> syncRunnable.run());
	}

	@Deprecated
	public static void setSyncRunnable(Consumer<ConfigFile> syncRunnable) {
		setSyncListener(new ConfigSyncListener() {
			@Override
			public boolean onSync(ConfigFile configFile) {
				syncRunnable.accept(configFile);
				return true;
			}

			@Override
			public boolean onFail(ConfigFile configFile) {
				return onSync(configFile);
			}
		});
	}

	public static void setSyncListener(ConfigSyncListener configSyncListener) {
		if (TweedClient.configSyncListener != null) {
			TweedClient.configSyncListener.onRemoved();
		}
		TweedClient.configSyncListener = configSyncListener;
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
					ConfigLoader.reloadAll(resourceManager, ConfigEnvironment.CLIENT, ConfigScope.SMALLEST);
				} catch (Throwable e) {
					Tweed.LOGGER.error("Tweed failed to load config files");
					e.printStackTrace();
				}
			}
		});
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer ->
				ConfigLoader.reloadAll(((MinecraftServerAccessor) minecraftServer).getServerResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD)
		);

		ClientPlayNetworking.registerGlobalReceiver(Tweed.CONFIG_SYNC_S2C_PACKET, (client, handler, packetByteBuf, packetSender) -> {
			ConfigOrigin origin = packetByteBuf.readEnumConstant(ConfigOrigin.class);
			String name = packetByteBuf.readString();

			if (name.charAt(0) == '!') { // file not known to server
				name = name.substring(1);
				ConfigFile file = TweedRegistry.getConfigFile(name);
				if (file == null) {
					Tweed.LOGGER.error("Received negative config sync packet for unknown file {}.\n" +
							"Please report to " + Tweed.MOD_ISSUES_URL, name);
				} else {
					if (configSyncListener != null) {
						if (configSyncListener.onFail(file)) {
							configSyncListener.onRemoved();
							configSyncListener = null;
						}
					}
				}
				return;
			}

			ConfigFile configFile = TweedRegistry.getConfigFile(name);
			if (configFile != null) {
				configFile.read(packetByteBuf, ConfigEnvironment.SERVER, ConfigScope.WORLD, origin);

				if (configSyncListener != null) {
					if (configSyncListener.onSync(configFile)) {
						configSyncListener.onRemoved();
						configSyncListener = null;
					}
				}
			} else {
				Tweed.LOGGER.info("Skipping config sync packet for unknown config file {}", name);
			}
		});
	}

	public static boolean isOnRemoteServer() {
		MinecraftClient client = MinecraftClient.getInstance();
		return ((client.world != null) && !client.isIntegratedServerRunning()) || ((client.getServer() != null) && client.getServer().isRemote());
	}
}
