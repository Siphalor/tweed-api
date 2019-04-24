package de.siphalor.tweed.server;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigLoader;
import de.siphalor.tweed.config.ConfigScope;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;

public class ServerCore implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		ServerStartCallback.EVENT.register(minecraftServer -> {
			Core.setMinecraftServer(minecraftServer);
			ConfigLoader.loadConfigs(minecraftServer.getDataManager(), ConfigEnvironment.SERVER, ConfigScope.GAME);
		});
	}
}
