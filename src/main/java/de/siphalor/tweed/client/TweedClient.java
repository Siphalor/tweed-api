package de.siphalor.tweed.client;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.TweedClientInitializer;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.tailor.ClothTailor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class TweedClient implements ClientModInitializer, TweedClientInitializer {
	private static Runnable syncRunnable;

	@Override
	public void registerClient() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			Registry.register(TweedRegistry.TAILORS, new Identifier(Tweed.MOD_ID, "cloth"), ClothTailor.INSTANCE);
		}
	}

	public static void setSyncRunnable(Runnable syncRunnable) {
		TweedClient.syncRunnable = syncRunnable;
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
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer ->
				ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD)
		);

		ClientPlayNetworking.registerGlobalReceiver(Tweed.CONFIG_SYNC_S2C_PACKET, (client, handler, packetByteBuf, packetSender) -> {
			ConfigOrigin origin = packetByteBuf.readEnumConstant(ConfigOrigin.class);
			String fileName = packetByteBuf.readString();
            for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
            	if(configFile.getName().equals(fileName)) {
					configFile.read(packetByteBuf, ConfigEnvironment.SERVER, ConfigScope.WORLD, origin);

					if (syncRunnable != null) {
						syncRunnable.run();
						syncRunnable = null;
					}
				}
			}
		});
	}

	public static boolean isOnRemoteServer() {
		MinecraftClient client = MinecraftClient.getInstance();
		return client.world != null && !client.isIntegratedServerRunning() || client.getServer() != null && client.getServer().isRemote();
	}
}
