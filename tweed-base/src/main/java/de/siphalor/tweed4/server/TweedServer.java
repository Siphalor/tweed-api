package de.siphalor.tweed4.server;

import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigLoader;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.mixin.MinecraftServerAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TweedServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
        ConfigLoader.initialReload(ConfigEnvironment.SERVER);
		ServerLifecycleEvents.SERVER_STARTED.register(server ->
				ConfigLoader.loadConfigs(((MinecraftServerAccessor) server).getServerResourceManager().getResourceManager(), ConfigEnvironment.SERVER, ConfigScope.WORLD)
		);
	}
}
