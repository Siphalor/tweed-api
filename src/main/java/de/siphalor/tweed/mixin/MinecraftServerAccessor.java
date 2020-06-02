package de.siphalor.tweed.mixin;

import net.minecraft.class_5350;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("field_25318")
	class_5350 getDataManagerHolder();
}
