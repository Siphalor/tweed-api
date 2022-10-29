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

package de.siphalor.tweed5.mixin.client;

import de.siphalor.tweed5.Tweed;
import de.siphalor.tweed5.TweedRegistries;
import de.siphalor.tweed5.config.*;
import de.siphalor.tweed5.reload.ReloadContext;
import de.siphalor.tweed5.reload.ReloadEnvironment;
import de.siphalor.tweed5.reload.ReloadScope;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Inject(method = "onGameJoin", at = @At("RETURN"), require = 0)
	public void onGameJoined(GameJoinS2CPacket packet, CallbackInfo callbackInfo) {
		for (ConfigFile configFile : TweedRegistries.CONFIG_FILES.getValues()) {
			if (!configFile.getRootCategory().matches(ReloadEnvironment.SYNCED, null)) {
				continue;
			}
			Tweed.LOGGER.info("Requested config sync for " + configFile.getName());
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeString(configFile.getName());
			ReloadContext.nonFile(ReloadEnvironment.SYNCED, ReloadScope.HIGHEST, true).write(packetByteBuf);
			ClientPlayNetworking.send(Tweed.REQUEST_SYNC_C2S_PACKET, packetByteBuf);
		}
	}
}
