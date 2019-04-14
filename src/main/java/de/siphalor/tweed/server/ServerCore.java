package de.siphalor.tweed.server;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigLoader;
import de.siphalor.tweed.config.ConfigScope;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class ServerCore implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		ServerStartCallback.EVENT.register(minecraftServer -> {
			Core.setMinecraftServer(minecraftServer);
			ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.SERVER, ConfigScope.GAME);
		});
		ServerStopCallback.EVENT.register((MinecraftServer minecraftServer) -> Core.setMinecraftServer(null));
		ResourceManagerHelper.get(ResourceType.DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Core.MODID, "resource_reload");
			}

			@Override
			public void apply(ResourceManager resourceManager) {
				ConfigLoader.loadConfigs(resourceManager, ConfigEnvironment.SERVER, ConfigScope.SMALLEST);
			}
		});
	}
}
