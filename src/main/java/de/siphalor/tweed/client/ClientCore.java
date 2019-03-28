package de.siphalor.tweed.client;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.ConfigDefinitionScope;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ClientCore implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.ASSETS).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Core.MODID, "assets_listener");
			}

			@Override
			public void apply(ResourceManager resourceManager) {
				ConfigLoader.loadConfigs(resourceManager, ConfigEnvironment.CLIENT, ConfigDefinitionScope.NONE);
			}
		});

		//noinspection CodeBlock2Expr
		ServerStartCallback.EVENT.register(minecraftServer -> {
			ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.SERVER, ConfigDefinitionScope.GAME);
		});

	}
}
