package de.siphalor.tweed;

import de.siphalor.tweed.config.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.hjson.HjsonOptions;

import java.io.File;

public class Core implements ModInitializer {
	public static final String MODID = "tweed";
	public static final Identifier CONFIG_SYNC_S2C_PACKET = new Identifier(MODID, "sync_config");
	public static final Identifier REQUEST_SYNC_C2S_PACKET = new Identifier(MODID, "request_sync");
	public static final Identifier TWEED_CLOTH_SYNC_C2S_PACKET = new Identifier(MODID, "sync_from_cloth_client");

	public static final char HJSON_PATH_DELIMITER = '.';
	public static final String mainConfigDirectory = FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + File.separator;
	public static final HjsonOptions HJSON_OPTIONS = new HjsonOptions().setAllowCondense(false).setBracesSameLine(true).setOutputComments(true).setSpace("\t");

	private static MinecraftServer minecraftServer;

	public static boolean isMinecraftServerReady() {
		return minecraftServer != null;
	}

	public static MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}

	public static void setMinecraftServer(MinecraftServer minecraftServer) {
		Core.minecraftServer = minecraftServer;
	}

	@Override
	public void onInitialize() {
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

		ServerSidePacketRegistry.INSTANCE.register(REQUEST_SYNC_C2S_PACKET, (packetContext, packetByteBuf) -> {
			String fileName = packetByteBuf.readString();
            for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
            	if(configFile.getName().equals(fileName)) {
            		if(packetContext.getPlayer().getServer().getPermissionLevel(packetContext.getPlayer().getGameProfile()) == 4) {
						configFile.syncToClient((ServerPlayerEntity) packetContext.getPlayer(), packetByteBuf.readEnumConstant(ConfigEnvironment.class), packetByteBuf.readEnumConstant(ConfigScope.class));
					} else {
            			packetByteBuf.readEnumConstant(ConfigEnvironment.class);
						configFile.syncToClient((ServerPlayerEntity) packetContext.getPlayer(), ConfigEnvironment.SYNCED, packetByteBuf.readEnumConstant(ConfigScope.class));
					}
            		break;
				}
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(TWEED_CLOTH_SYNC_C2S_PACKET, ((packetContext, packetByteBuf) -> {
			String fileName = packetByteBuf.readString();
			for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
				if(configFile.getName().equals(fileName)) {
					if(packetContext.getPlayer().getServer().getPermissionLevel(packetContext.getPlayer().getGameProfile()) == 4) {
						ConfigEnvironment environment = packetByteBuf.readEnumConstant(ConfigEnvironment.class);
						ConfigScope scope = packetByteBuf.readEnumConstant(ConfigScope.class);
						configFile.read(packetByteBuf, environment, ConfigScope.SMALLEST);
						ConfigLoader.writeMainConfigFile(configFile, environment, scope);
					} else {
                        packetByteBuf.clear();
					}
					break;
				}
			}
		}));
	}
}
