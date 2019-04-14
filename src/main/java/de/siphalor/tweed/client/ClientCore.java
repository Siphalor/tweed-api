package de.siphalor.tweed.client;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigLoader;
import de.siphalor.tweed.config.ConfigScope;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
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
				ConfigLoader.loadConfigs(resourceManager, ConfigEnvironment.CLIENT, ConfigScope.SMALLEST);
			}
		});

		ServerStartCallback.EVENT.register(minecraftServer -> {
			Core.setMinecraftServer(minecraftServer);
			ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.SERVER, ConfigScope.GAME);
		});
		//noinspection CodeBlock2Expr
		ServerStopCallback.EVENT.register(minecraftServer -> {
			Core.setMinecraftServer(null);
		});

		ClientSidePacketRegistry.INSTANCE.register(Core.CONFIG_SYNC_PACKET, (packetContext, packetByteBuf) -> {
			//ConfigFile = TweedRegistry.getConfigFiles().stream().;
		});
	}
}
