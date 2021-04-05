package de.siphalor.tweed.annotated;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.TweedInitializer;
import de.siphalor.tweed.config.ConfigFile;
import de.siphalor.tweed.config.TweedRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class TweedAnnotatedInitializer implements TweedInitializer {
	@Override
	public void tweedInit() {
		List<EntrypointContainer<Object>> entrypoints = FabricLoader.getInstance().getEntrypointContainers(Tweed.MOD_ID + ":config", Object.class);

		for (EntrypointContainer<Object> entrypoint : entrypoints) {
			try {
				ConfigFile configFile = POJOConverter.toConfigFile(entrypoint.getEntrypoint(), entrypoint.getProvider().getMetadata().getId());
				TweedRegistry.registerConfigFile(configFile);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
}
