package de.siphalor.tweed.client;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class ClientCore implements ClientModInitializer {
	public static TweedClothBridge scheduledClothBridge;

	public static MinecraftServer getMinecraftServer() {
		return ((MinecraftClient) FabricLoader.getInstance().getGameInstance()).getServer();
	}

	@Override
	public void onInitializeClient() {
        ConfigLoader.initialReload(ConfigEnvironment.UNIVERSAL);

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Tweed.MOD_ID, "assets_listener");
			}

			@Override
			public void apply(ResourceManager resourceManager) {
				try {
					ConfigLoader.loadConfigs(resourceManager, ConfigEnvironment.CLIENT, ConfigScope.SMALLEST);
				} catch (Throwable e) {
					Tweed.LOGGER.error("Tweed failed to load config files");
					e.printStackTrace();
				}
			}
		});
		ServerStartCallback.EVENT.register(minecraftServer -> ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD));

		ClientSidePacketRegistry.INSTANCE.register(Tweed.CONFIG_SYNC_S2C_PACKET, (packetContext, packetByteBuf) -> {
			ConfigOrigin origin = packetByteBuf.readEnumConstant(ConfigOrigin.class);
			String fileName = packetByteBuf.readString();
            for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
            	if(configFile.getName().equals(fileName)) {
					configFile.read(packetByteBuf, ConfigEnvironment.SERVER, ConfigScope.WORLD, origin);

					if(scheduledClothBridge != null) {
						scheduledClothBridge.onSync(configFile);
					}
					break;
				}
			}
		});
	}
}
