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
import de.siphalor.tweed4.client.CustomNoticeScreen;
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
	public abstract Map<String, ScreenTailorScreenFactory<?>> getScreenFactories();

	protected Screen syncAndCreateScreen(ConfigFile configFile, ScreenTailorScreenFactory<?> screenFactory, Screen parent) {
		return syncAndCreateScreen(Collections.singletonList(configFile), screenFactory, parent);
	}

	protected Screen syncAndCreateScreen(Collection<ConfigFile> configFiles, ScreenTailorScreenFactory<?> screenFactory, Screen parentScreen) {
		MinecraftClient client = MinecraftClient.getInstance();

		sync:
		if (client.world != null) {
			if (!ClientPlayNetworking.canSend(Tweed.REQUEST_SYNC_C2S_PACKET)) {
				break sync;
			}

			List<ConfigFile> syncFiles = new ArrayList<>();
			for (ConfigFile configFile : configFiles) {
				if (configFile.getRootCategory().matches(ConfigEnvironment.SERVER, null)) {
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

						TweedClient.setSyncRunnable(file -> {
							awaitedSyncs.getAndUpdate(files -> {
								if (files != null) {
									files.remove(file);
								}
								return files;
							});
							List<ConfigFile> captured = awaitedSyncs.get();
							if (captured != null && captured.isEmpty()) {
								client.openScreen(screenFactory.create(parentScreen));
							}
						});
					},
					() -> {
						awaitedSyncs.set(null);
						client.openScreen(parentScreen);
					},
					new TranslatableText("tweed4_tailor_screen.syncFromServer"),
					new TranslatableText("tweed4_tailor_screen.syncFromServer.note")
			);
		}

		return screenFactory.create(parentScreen);
	}

	protected void save(ConfigFile configFile) {
		if (TweedClient.isOnRemoteServer()) {
			configFile.syncToServer(ConfigEnvironment.UNIVERSAL, ConfigScope.SMALLEST);
			ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
			ConfigLoader.loadConfigs(MinecraftClient.getInstance().getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.SMALLEST);
		} else {
			ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
			ConfigLoader.loadConfigs(MinecraftClient.getInstance().getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD);
		}
	}
}
