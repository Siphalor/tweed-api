package de.siphalor.tweed;

import de.siphalor.tweed.config.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Tweed implements ModInitializer {
	public static final String MOD_ID = "tweed";
	public static final Identifier CONFIG_SYNC_S2C_PACKET = new Identifier(MOD_ID, "sync_config");
	public static final Identifier REQUEST_SYNC_C2S_PACKET = new Identifier(MOD_ID, "request_sync");
	public static final Identifier TWEED_CLOTH_SYNC_C2S_PACKET = new Identifier(MOD_ID, "sync_from_cloth_client");

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final char PATH_DELIMITER = '.';
	public static final String mainConfigDirectory = FabricLoader.getInstance().getConfigDir().toFile().getAbsolutePath() + File.separator;

	public static final List<MinecraftServer> MINECRAFT_SERVERS = new LinkedList<>();

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Tweed.MOD_ID, "data_listener");
			}

			@Override
			public void apply(ResourceManager resourceManager) {
				try {
					ConfigLoader.loadConfigs(resourceManager, ConfigEnvironment.SERVER, ConfigScope.SMALLEST);
				} catch (Throwable e) {
					Tweed.LOGGER.error("Tweed failed to load config files:");
					e.printStackTrace();
				}
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(REQUEST_SYNC_C2S_PACKET, (server, player, handler, packetByteBuf, packetSender) -> {
			String fileName = packetByteBuf.readString(32767);
            for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
            	if(configFile.getName().equals(fileName)) {
            		if(server.getPermissionLevel(player.getGameProfile()) == 4) {
						configFile.syncToClient(player, packetByteBuf.readEnumConstant(ConfigEnvironment.class), packetByteBuf.readEnumConstant(ConfigScope.class), packetByteBuf.readEnumConstant(ConfigOrigin.class));
					} else {
            			packetByteBuf.readEnumConstant(ConfigEnvironment.class);
            			ConfigScope scope = packetByteBuf.readEnumConstant(ConfigScope.class);
            			packetByteBuf.readEnumConstant(ConfigOrigin.class);
						configFile.syncToClient(player, ConfigEnvironment.SYNCED, scope, ConfigOrigin.DATAPACK);
					}
            		break;
				}
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(TWEED_CLOTH_SYNC_C2S_PACKET, (server, player, handler, packetByteBuf, packetSender) -> {
			String fileName = packetByteBuf.readString(32767);
			for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
				if(configFile.getName().equals(fileName)) {
					if(server.getPermissionLevel(player.getGameProfile()) == 4) {
						ConfigEnvironment environment = packetByteBuf.readEnumConstant(ConfigEnvironment.class);
						ConfigScope scope = packetByteBuf.readEnumConstant(ConfigScope.class);
						configFile.read(packetByteBuf, environment, ConfigScope.SMALLEST, ConfigOrigin.MAIN);
						ConfigLoader.updateMainConfigFile(configFile, environment, scope);
					} else {
                        packetByteBuf.clear();
					}
					break;
				}
			}
		});

		Tweed.runEntryPoints();
	}

	public static void runEntryPoints() {
		FabricLoader loaderAPI = FabricLoader.getInstance();

		{
			List<TweedInitializer> initializers = loaderAPI.getEntrypoints(Tweed.MOD_ID + ":init", TweedInitializer.class);
			initializers.forEach(TweedInitializer::register);
			initializers.forEach(TweedInitializer::init);
		}

		if (loaderAPI.getEnvironmentType() == EnvType.CLIENT) {
			List<TweedClientInitializer> initializers = loaderAPI.getEntrypoints(Tweed.MOD_ID + ":client_init", TweedClientInitializer.class);
			initializers.forEach(TweedClientInitializer::registerClient);
		}

		List<EntrypointContainer<Object>> entrypoints = loaderAPI.getEntrypointContainers(Tweed.MOD_ID + ":config", Object.class);

		for (EntrypointContainer<Object> entrypoint : entrypoints) {
			try {
				TweedRegistry.registerConfigPOJO(entrypoint.getEntrypoint(), entrypoint.getProvider().getMetadata().getId());
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

}
