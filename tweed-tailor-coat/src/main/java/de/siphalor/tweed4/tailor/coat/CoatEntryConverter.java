package de.siphalor.tweed4.tailor.coat;

import de.siphalor.coat.list.entry.ConfigListConfigEntry;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;

import java.util.List;

@FunctionalInterface
public interface CoatEntryConverter<S> {
	List<ConfigListConfigEntry<?>> convert(ValueConfigEntry<S> configEntry, String path);
}
