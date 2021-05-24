package de.siphalor.tweed4.mixin;

import de.siphalor.tweed4.Tweed;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstructed(CallbackInfo callbackInfo) {
		Tweed.MINECRAFT_SERVERS.add((MinecraftServer) (Object) this);
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	public void onShutdown(CallbackInfo callbackInfo) {
		Tweed.MINECRAFT_SERVERS.remove((MinecraftServer) (Object) this);
	}
}
