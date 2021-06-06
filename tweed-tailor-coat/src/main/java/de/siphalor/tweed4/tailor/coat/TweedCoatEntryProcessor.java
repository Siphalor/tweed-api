package de.siphalor.tweed4.tailor.coat;

import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;

@FunctionalInterface
public interface TweedCoatEntryProcessor<S> {
	void process(ConfigListWidget parentWidget, ValueConfigEntry<S> configEntry, String path);
}
