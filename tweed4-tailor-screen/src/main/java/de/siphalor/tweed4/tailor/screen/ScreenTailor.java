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

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.client.ConfigSyncListener;
import de.siphalor.tweed4.client.TweedClient;
import de.siphalor.tweed4.config.*;
import de.siphalor.tweed4.tailor.Tailor;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ScreenTailor extends Tailor {
	/**
	 * Returns the screen factories for display with Mod Menu.
	 * @return A map from mod id to screen factory
	 */
	public abstract Map<String, ScreenTailorScreenFactory<?>> getScreenFactories();

	protected Screen syncAndCreateScreen(ConfigFile configFile, ScreenTailorScreenFactory<?> screenFactory, Screen parent) {
		return syncAndCreateScreen(Collections.singletonList(configFile), screenFactory, parent);
	}

	/**
	 * Syncs the config data from the server and creates a screen. <br />
	 * If necessary, a sync will be performed as soon as the returned screen is opened.
	 *
	 * @param configFiles The config files to sync
	 * @param screenFactory The screen factory to use
	 * @param parentScreen The parent screen to return to
	 * @return The created screen to open
	 */
	protected Screen syncAndCreateScreen(Collection<ConfigFile> configFiles, ScreenTailorScreenFactory<?> screenFactory, Screen parentScreen) {
		MinecraftClient client = MinecraftClient.getInstance();

		sync:
		if (client.world != null) {
			if (!ClientPlayNetworking.canSend(Tweed.REQUEST_SYNC_C2S_PACKET)) {
				break sync;
			}

			List<ConfigFile> syncFiles = new ArrayList<>();
			for (ConfigFile configFile : configFiles) {
				if (isSyncFromServerRequired(configFile)) {
					syncFiles.add(configFile);
				}
			}
			if (syncFiles.isEmpty()) {
				break sync;
			}

			AtomicReference<List<ConfigFile>> awaitedSyncs = new AtomicReference<>(syncFiles);
			return new CustomNoticeScreen(
					() -> {
						PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
						for (ConfigFile syncFile : syncFiles) {
							buf.writeString(syncFile.getName());
							buf.writeEnumConstant(ConfigEnvironment.UNIVERSAL);
							buf.writeEnumConstant(ConfigScope.SMALLEST);
							buf.writeEnumConstant(ConfigOrigin.MAIN);
						}
						ClientPlayNetworking.send(Tweed.REQUEST_SYNC_C2S_PACKET, buf);

						TweedClient.setSyncListener(new ConfigSyncListener() {
							@Override
							public boolean onSync(ConfigFile configFile) {
								awaitedSyncs.getAndUpdate(files -> {
									if (files != null) {
										files.remove(configFile);
									}
									return files;
								});
								List<ConfigFile> captured = awaitedSyncs.get();
								if (captured != null && captured.isEmpty()) {
                                    client.submit(() -> client.setScreen(screenFactory.create(parentScreen)));
									return true;
								}
								return false;
							}

							@Override
							public boolean onFail(ConfigFile configFile) {
								return onSync(configFile);
							}
						});
					},
					() -> {
						awaitedSyncs.set(null);
                        client.submit(() -> client.setScreen(parentScreen));
					},
					new TranslatableText("tweed4_tailor_screen.syncFromServer"),
					new TranslatableText("tweed4_tailor_screen.syncFromServer.note")
			);
		}

		return screenFactory.create(parentScreen);
	}

	/**
	 * Checks whether the config file requires to be synced before being opened.
	 * @param configFile The config file to check
	 * @return Whether the config file requires to be synced before being opened
	 */
	protected boolean isSyncFromServerRequired(ConfigFile configFile) {
		return configFile.getRootCategory().matches(ConfigEnvironment.SERVER, null);
	}

	/**
	 * Saves the config data and performs a sync to the server, if applicable.
	 * @param configFile The config file to save
	 * @see ScreenTailor#saveLocally(ConfigFile)
	 * @see ScreenTailor#isSyncToServerApplicable(ConfigFile)
	 */
	protected void save(ConfigFile configFile) {
		if (isSyncToServerApplicable(configFile)) {
			configFile.syncToServer(ConfigEnvironment.UNIVERSAL, ConfigScope.SMALLEST);
		}
		saveLocally(configFile);
	}

	/**
	 * Saves the config data, without performing a sync, even if applicable.
	 * @param configFile The config file to save
	 * @see ScreenTailor#isSyncToServerApplicable(ConfigFile)
	 * @see ScreenTailor#save(ConfigFile)
	 */
	protected void saveLocally(ConfigFile configFile) {
		MinecraftClient client = MinecraftClient.getInstance();
		ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
		if (client.world == null) {
			// Player is in main menu, reload in world scope
			ConfigLoader.reload(configFile, client.getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD);
		} else {
			// Player is somewhere in game, reload in the smallest scope
			ConfigLoader.reload(configFile, client.getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.SMALLEST);
		}
	}

	/**
	 * Checks whether the config file is applicable for syncing in the current world. <br />
	 * This considers whether the sync is necessary and if the player is allowed to perform a sync in the current world.
	 * @param configFile The config file to check
	 * @return Whether the config file is applicable for syncing in the current world
	 */
	protected boolean isSyncToServerApplicable(ConfigFile configFile) {
		if (TweedClient.isOnRemoteServer()) {
			assert MinecraftClient.getInstance().player != null;
			return configFile.getRootCategory().matches(ConfigEnvironment.SERVER, null)
					&& MinecraftClient.getInstance().player.hasPermissionLevel(3);
		}
		return false;
	}
}
