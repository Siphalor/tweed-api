package de.siphalor.tweed.server;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.ConfigDefinitionScope;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigLoader;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ServerCore implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		//noinspection CodeBlock2Expr
		ServerStartCallback.EVENT.register(minecraftServer -> {
			ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.SERVER, ConfigDefinitionScope.GAME);
		});
		ResourceManagerHelper.get(ResourceType.DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Core.MODID, "resource_reload");
			}

			@Override
			public void apply(ResourceManager resourceManager) {
				ConfigLoader.loadConfigs(resourceManager, ConfigEnvironment.SERVER, ConfigDefinitionScope.NONE);
			}
		});
	}
}
