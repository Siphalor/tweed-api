package de.siphalor.tweed;

import de.siphalor.tweed.client.TweedClient;
import de.siphalor.tweed.client.TweedClothBridge;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.annotated.AConfigEntry;
import de.siphalor.tweed.config.annotated.AConfigExclude;
import de.siphalor.tweed.config.annotated.AConfigTransitive;
import de.siphalor.tweed.config.annotated.ATweedConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

public class Tweed implements ModInitializer {
	public static final String MOD_ID = "tweed";
	public static final Identifier CONFIG_SYNC_S2C_PACKET = new Identifier(MOD_ID, "sync_config");
	public static final Identifier REQUEST_SYNC_C2S_PACKET = new Identifier(MOD_ID, "request_sync");
	public static final Identifier TWEED_CLOTH_SYNC_C2S_PACKET = new Identifier(MOD_ID, "sync_from_cloth_client");

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final char PATH_DELIMITER = '.';
	public static final String mainConfigDirectory = FabricLoader.getInstance().getConfigDirectory().getAbsolutePath() + File.separator;

	public static ConfigFile configFile;
	public static TweedClothBridge tweedClothBridge;

	public static MinecraftServer getMinecraftServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? TweedClient.getMinecraftServer() : (MinecraftServer) FabricLoader.getInstance().getGameInstance();
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Tweed.MOD_ID, "resource_reload");
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

		ServerSidePacketRegistry.INSTANCE.register(REQUEST_SYNC_C2S_PACKET, (packetContext, packetByteBuf) -> {
			String fileName = packetByteBuf.readString(32767);
            for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
            	if(configFile.getName().equals(fileName)) {
            		if(Objects.requireNonNull(packetContext.getPlayer().getServer()).getPermissionLevel(packetContext.getPlayer().getGameProfile()) == 4) {
						configFile.syncToClient((ServerPlayerEntity) packetContext.getPlayer(), packetByteBuf.readEnumConstant(ConfigEnvironment.class), packetByteBuf.readEnumConstant(ConfigScope.class), packetByteBuf.readEnumConstant(ConfigOrigin.class));
					} else {
            			packetByteBuf.readEnumConstant(ConfigEnvironment.class);
            			ConfigScope scope = packetByteBuf.readEnumConstant(ConfigScope.class);
            			packetByteBuf.readEnumConstant(ConfigOrigin.class);
						configFile.syncToClient((ServerPlayerEntity) packetContext.getPlayer(), ConfigEnvironment.SYNCED, scope, ConfigOrigin.DATAPACK);
					}
            		break;
				}
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(TWEED_CLOTH_SYNC_C2S_PACKET, ((packetContext, packetByteBuf) -> {
			String fileName = packetByteBuf.readString(32767);
			for(ConfigFile configFile : TweedRegistry.getConfigFiles()) {
				if(configFile.getName().equals(fileName)) {
					if(Objects.requireNonNull(packetContext.getPlayer().getServer()).getPermissionLevel(packetContext.getPlayer().getGameProfile()) == 4) {
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
		}));

		Test test = new Test();
		configFile = TweedRegistry.registerPOJO("hey", test);
		tweedClothBridge = new TweedClothBridge(configFile);
	}

	@ATweedConfig(scope = ConfigScope.GAME, environment = ConfigEnvironment.UNIVERSAL)
	public static class Test {
		@AConfigEntry(name = "bool", comment = "Some kind of Boolean")
		Boolean aBoolean = true;

		boolean primBool = false;

		@AConfigExclude
		String test = "abc";

		Integer number = 123;

		@AConfigEntry(comment = "This is an object")
		A a;

		@AConfigTransitive
		Trans trans;

		ConfigScope scope = ConfigScope.DEFAULT;

		public static class A {
			String name = "Siphalor";
		}

		public static class Trans {
			String type = "blob";
		}
	}
}
