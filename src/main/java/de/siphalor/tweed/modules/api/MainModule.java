package de.siphalor.tweed.modules.api;

import de.siphalor.tweed.config.ConfigCategory;

public class MainModule extends Module {
	public MainModule(String id, String description) {
		super(id, description);
	}

	public void setup() {
		if(backgroundTexture != null) {
			configCategory.setBackgroundTexture(backgroundTexture);
		}
		configCategory.setComment(description);
		configEntries.forEach(entry -> configCategory.register(entry.getLeft(), entry.getRight()));
		features.forEach(feature -> feature.getConfigEntries().forEach(pair -> configCategory.register(pair.getLeft(), pair.getRight())));
	}

	public final void setRootCategory(ConfigCategory category) {
		configCategory = category;
		setup();
	}

	public final String getId() {
		return id;
	}
}
