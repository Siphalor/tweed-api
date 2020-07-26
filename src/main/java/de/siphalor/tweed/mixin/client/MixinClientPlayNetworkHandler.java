package de.siphalor.tweed.mixin.client;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.*;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
			ClientSidePacketRegistry.INSTANCE.sendToServer(Tweed.REQUEST_SYNC_C2S_PACKET, packetByteBuf);
		}
	}
}
