package de.siphalor.tweed.mixin.client;

import com.google.common.collect.ImmutableMap;
import de.siphalor.tweed.tailor.ClothTailor;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Function;

/**
 * This mixin is required as Modmenu 1.7 has no way to register config screens for other mods
 */
@Mixin(targets = "io/github/prospector/modmenu/ModMenu", remap = false)
public class MixinModMenu {
	@Inject(method = "onInitializeClient", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void beforeScreenFactoryBuild(CallbackInfo callbackInfo, ImmutableMap.Builder<String, Function<Screen, ? extends Screen>> factoriesBuilder) {
		factoriesBuilder.putAll(ClothTailor.INSTANCE.getScreenFactories());
	}

}
