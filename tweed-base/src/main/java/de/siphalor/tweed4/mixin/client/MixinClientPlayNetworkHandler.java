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

package de.siphalor.tweed4.mixin.client;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.config.*;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Inject(method = "onGameJoin", at = @At("RETURN"), require = 0)
	public void onGameJoined(GameJoinS2CPacket packet, CallbackInfo callbackInfo) {
		for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
			Tweed.LOGGER.info("Requested config sync for " + configFile.getName());
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeString(configFile.getName());
			packetByteBuf.writeEnumConstant(ConfigEnvironment.SYNCED);
			packetByteBuf.writeEnumConstant(ConfigScope.WORLD);
			packetByteBuf.writeEnumConstant(ConfigOrigin.DATAPACK);
			ClientPlayNetworking.send(Tweed.REQUEST_SYNC_C2S_PACKET, packetByteBuf);
		}
	}
}
