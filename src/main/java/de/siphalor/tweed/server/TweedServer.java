package de.siphalor.tweed.server;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigLoader;
import de.siphalor.tweed.config.ConfigScope;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TweedServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
        ConfigLoader.initialReload(ConfigEnvironment.SERVER);
		ServerLifecycleEvents.SERVER_STARTED.register(server ->
				ConfigLoader.loadConfigs(server.getDataManager(), ConfigEnvironment.SERVER, ConfigScope.GAME)
		);
	}
}
