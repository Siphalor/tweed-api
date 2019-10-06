package de.siphalor.tweed.modules.api;

import de.siphalor.tweed.config.ConfigCategory;
import de.siphalor.tweed.config.entry.ConfigEntry;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.Collections;

public abstract class SubModule extends Module {
	public SubModule(String name, String description) {
		super(name, description);
		configCategory = new ConfigCategory();
		configCategory.setComment(description);
	}

	protected ConfigCategory getCombinedConfigCategory() {
		if(configCategory.isEmpty() && (!configEntries.isEmpty() || !features.isEmpty())) {
			if (backgroundTexture != null)
				configCategory.setBackgroundTexture(backgroundTexture);
			configEntries.forEach(pair -> configCategory.register(pair.getLeft(), pair.getRight()));
			features.forEach(feature -> feature.getConfigEntries().forEach(pair -> configCategory.register(pair.getLeft(), pair.getRight())));
		}
		return configCategory;
	}

	@Override
	public Collection<Pair<String, ConfigEntry>> getConfigEntries() {
        return Collections.singleton(new Pair<>(id, getCombinedConfigCategory()));
	}
}
