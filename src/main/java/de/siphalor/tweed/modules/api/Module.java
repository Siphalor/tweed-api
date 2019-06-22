package de.siphalor.tweed.modules.api;

import de.siphalor.tweed.config.ConfigCategory;
import de.siphalor.tweed.modules.api.features.Feature;
import de.siphalor.tweed.modules.api.features.OptionalFeature;
import net.minecraft.util.Identifier;

import java.util.concurrent.ConcurrentLinkedQueue;

abstract class Module extends OptionalFeature {

    public String id;
    public String description;
    protected ConcurrentLinkedQueue<Feature> features;
    protected Identifier backgroundTexture;
    protected ConfigCategory configCategory;

    public Module(String id, String description) {
        super("enabled", "Enables/disables this module.");
        this.description = description;
        this.id = id;
        this.features = new ConcurrentLinkedQueue<>();
    }

    public <T extends Feature> T register(T feature) {
        features.add(feature);
        return feature;
    }

    protected void setBackgroundTexture(Identifier identifier) {
        backgroundTexture = identifier;
    }

    public ConfigCategory getConfigCategory() {
        return configCategory;
    }
}