/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4;

import de.siphalor.tweed4.config.*;
import de.siphalor.tweed4.data.serializer.ConfigDataSerializer;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Tweed implements ModInitializer {
	public static final String MOD_ID = "tweed4";
	public static final String MOD_NAME = "Tweed 4";
	public static final String MOD_ISSUES_URL = "https://github.com/Siphalor/tweed-api/issues";
	public static final Identifier CONFIG_SYNC_S2C_PACKET = new Identifier(MOD_ID, "sync_config");
	public static final Identifier REQUEST_SYNC_C2S_PACKET = new Identifier(MOD_ID, "request_sync");
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static final Identifier TWEED_CLOTH_SYNC_C2S_PACKET = new Identifier(MOD_ID, "sync_from_cloth_client");
	public static final Identifier CONFIG_SYNC_C2S_PACKET = new Identifier(MOD_ID, "sync_config_from_client");

	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final char PATH_DELIMITER = '.';
	public static final String mainConfigDirectory = FabricLoader.getInstance().getConfigDir().toFile().getAbsolutePath() + File.separator;

	public static final List<MinecraftServer> MINECRAFT_SERVERS = new LinkedList<>();

	private static boolean entrypointsRun = false;

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Tweed.MOD_ID, "data_listener");
			}

			@Override
			public void reload(ResourceManager resourceManager) {
				try {
					ConfigLoader.reloadAll(resourceManager, ConfigEnvironment.SERVER, ConfigScope.SMALLEST);
				} catch (Throwable e) {
					Tweed.LOGGER.error("Tweed failed to load config files:");
					e.printStackTrace();
				}
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(REQUEST_SYNC_C2S_PACKET, (server, player, handler, packetByteBuf, packetSender) -> {
			while (packetByteBuf.isReadable()) {
				String name = packetByteBuf.readString(32767);
				ConfigEnvironment environment = packetByteBuf.readEnumConstant(ConfigEnvironment.class);
				ConfigScope scope = packetByteBuf.readEnumConstant(ConfigScope.class);
				ConfigOrigin origin = packetByteBuf.readEnumConstant(ConfigOrigin.class);
				ConfigFile configFile = TweedRegistry.getConfigFile(name);
				if (configFile != null) {
					if (server.getPermissionLevel(player.getGameProfile()) == 4) {
						configFile.syncToClient(player, environment, scope, origin);
					} else {
						configFile.syncToClient(player, ConfigEnvironment.SYNCED, scope, ConfigOrigin.DATAPACK);
					}
				} else {
					LOGGER.warn("Received request to sync config file " + name + " but it was not found.");
					PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
					buf.writeEnumConstant(ConfigOrigin.DATAPACK);
					buf.writeString("!" + name);
					ServerPlayNetworking.send(player, CONFIG_SYNC_S2C_PACKET, buf);
				}
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(CONFIG_SYNC_C2S_PACKET, Tweed::receiveSyncC2SPacket);
		ServerPlayNetworking.registerGlobalReceiver(TWEED_CLOTH_SYNC_C2S_PACKET, Tweed::receiveSyncC2SPacket);

		Tweed.runEntryPoints();
	}

	private static void receiveSyncC2SPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
		String name = packetByteBuf.readString(32767);
		ConfigFile configFile = TweedRegistry.getConfigFile(name);
		if (configFile != null) {
			if (server.getPermissionLevel(player.getGameProfile()) == 4) {
				ConfigEnvironment environment = packetByteBuf.readEnumConstant(ConfigEnvironment.class);
				ConfigScope scope = packetByteBuf.readEnumConstant(ConfigScope.class);
				configFile.read(packetByteBuf, environment, ConfigScope.SMALLEST, ConfigOrigin.MAIN);
				ConfigLoader.updateMainConfigFile(configFile, environment, scope);
			} else {
				packetByteBuf.clear();
			}
		}
		LOGGER.warn("Received request to sync config file " + name + " but it was not found.");
	}

	/**
	 * Runs the entry points of Tweed. <br />
	 * This will always only be called once, but is not thread-safe and therefore should only be called from the main thread.
	 */
	public static void runEntryPoints() {
		if (entrypointsRun) {
			return;
		}
		entrypointsRun = true;

		FabricLoader loaderAPI = FabricLoader.getInstance();

		{
			// noinspection RedundantSuppression,deprecation,rawtypes
			List<ConfigDataSerializer> serializers = loaderAPI.getEntrypoints(Tweed.MOD_ID + ":serializer", ConfigDataSerializer.class);
			//noinspection deprecation
			for (ConfigDataSerializer<?, ?, ?> serializer : serializers) {
				if (serializer.getId() == null) {
					LOGGER.error("Failed to register serializer of kind " + serializer.getClass().getName() + " through entrypoint " +
							"because it has no `getId()` method declared!");
					continue;
				}
				Registry.register(TweedRegistry.SERIALIZERS, Identifier.tryParse(serializer.getId()), serializer);
				if ("tweed4:hjson".equals(serializer.getId())) {
					//noinspection deprecation
					TweedRegistry.setDefaultSerializer(serializer);
				}
			}
		}

		if (loaderAPI.getEnvironmentType() == EnvType.CLIENT) {
			List<TweedClientInitializer> initializers = loaderAPI.getEntrypoints(Tweed.MOD_ID + ":client_init", TweedClientInitializer.class);
			initializers.forEach(TweedClientInitializer::tweedRegisterClient);
		}

		{
			List<TweedInitializer> initializers = loaderAPI.getEntrypoints(Tweed.MOD_ID + ":init", TweedInitializer.class);
			initializers.forEach(TweedInitializer::tweedRegister);
			initializers.forEach(TweedInitializer::tweedInit);
		}

		if (TweedRegistry.getDefaultSerializer() == null) {
			//noinspection deprecation
			TweedRegistry.setDefaultSerializer(TweedRegistry.SERIALIZERS.streamEntries().findFirst().get().value());
		}

		if (loaderAPI.getEnvironmentType() == EnvType.CLIENT) {
			List<TweedClientInitializer> initializers = loaderAPI.getEntrypoints(Tweed.MOD_ID + ":client_init", TweedClientInitializer.class);
			initializers.forEach(TweedClientInitializer::tweedInitClient);
		}
	}

	public static ConfigEnvironment getCurrentEnvironment() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return ConfigEnvironment.SERVER;
		} else {
			return ConfigEnvironment.CLIENT;
		}
	}

}
