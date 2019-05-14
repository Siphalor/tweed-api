package de.siphalor.tweed.client;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.entry.AbstractValueEntry;
import de.siphalor.tweed.config.entry.ConfigEntry;
import de.siphalor.tweed.util.Recursive;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Consumer;

public class ClientCore implements ClientModInitializer {
	public static TweedClothBridge scheduledClothBridge;

	@Override
	public void onInitializeClient() {
        ConfigLoader.initialReload(ConfigEnvironment.UNIVERSAL);

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
			ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD);
		});

		ClientSidePacketRegistry.INSTANCE.register(Core.CONFIG_SYNC_S2C_PACKET, (packetContext, packetByteBuf) -> {
			String fileName = packetByteBuf.readString();
            for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
            	if(configFile.getName().equals(fileName)) {
					configFile.read(packetByteBuf, ConfigEnvironment.SERVER, ConfigScope.WORLD);

					Recursive<Consumer<Map.Entry<String, ConfigEntry>>> recursive = new Recursive<>();
					recursive.lambda = entry -> {
						if(entry.getValue().getEnvironment() != ConfigEnvironment.CLIENT) {
							if(entry.getValue() instanceof ConfigCategory) {
								((ConfigCategory) entry.getValue()).entryStream().forEach(recursive.lambda);
							} else if(entry.getValue() instanceof AbstractValueEntry) {
								//noinspection unchecked
								((AbstractValueEntry) entry.getValue()).setMainConfigValue(((AbstractValueEntry) entry.getValue()).value);
							}
						}
					};

					configFile.getRootCategory().entryStream().forEach(recursive.lambda);

					if(scheduledClothBridge != null) {
						scheduledClothBridge.onSync(configFile);
					}
					break;
				}
			}
		});
	}
}
