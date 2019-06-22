package de.siphalor.tweed.modules.api.features;

import de.siphalor.tweed.config.entry.ConfigEntry;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Feature {
	protected Queue<Pair<String, ConfigEntry>> configEntries;

	public Feature() {
		configEntries = new ConcurrentLinkedQueue<>();
	}

	public final <T extends ConfigEntry> T register(String name, T configEntry) {
		configEntries.add(new Pair<>(name, configEntry));
		return configEntry;
	}

	public Collection<Pair<String, ConfigEntry>> getConfigEntries() {
		return configEntries;
	}

    public static String formatName(String name) {
        return WordUtils.capitalizeFully(name.replace("_", " "));
	}
}
