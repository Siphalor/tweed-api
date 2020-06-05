package de.siphalor.tweed.server;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigLoader;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.mixin.MinecraftServerAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;

public class TweedServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
        ConfigLoader.initialReload(ConfigEnvironment.SERVER);
		ServerStartCallback.EVENT.register(minecraftServer -> {
			ConfigLoader.loadConfigs(((MinecraftServerAccessor) minecraftServer).getServerResourceManager().getResourceManager(), ConfigEnvironment.SERVER, ConfigScope.GAME);
		});
	}
}
