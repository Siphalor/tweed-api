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

import java.util.Map;

public abstract class ScreenTailor extends Tailor {
	protected boolean waitingForFile = false;

	public abstract Map<String, ScreenTailorScreenFactory<?>> getScreenFactories();

	protected Screen syncAndCreateScreen(ConfigFile configFile, ScreenTailorScreenFactory<?> screenFactory, Screen parentScreen) {
		boolean inGame = MinecraftClient.getInstance().world != null;
		if (inGame && configFile.getRootCategory().getEnvironment() != ConfigEnvironment.CLIENT) {
			return new CustomNoticeScreen(
					() -> {
						waitingForFile = true;

						PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
						buf.writeString(configFile.getName());
						buf.writeEnumConstant(ConfigEnvironment.UNIVERSAL);
						buf.writeEnumConstant(ConfigScope.SMALLEST);
						buf.writeEnumConstant(ConfigOrigin.MAIN);
						ClientPlayNetworking.send(Tweed.REQUEST_SYNC_C2S_PACKET, buf);

						TweedClient.setSyncRunnable(() -> {
							if (waitingForFile) {
								waitingForFile = false;
								MinecraftClient.getInstance().openScreen(screenFactory.create(parentScreen));
							}
						});
					},
					() -> {
						waitingForFile = false;
						MinecraftClient.getInstance().openScreen(parentScreen);
					},
					new TranslatableText("tweed4_tailor_screen.syncFromServer"),
					new TranslatableText("tweed4_tailor_screen.syncFromServer.note")
			);
		} else {
			return screenFactory.create(parentScreen);
		}
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
