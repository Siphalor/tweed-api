package de.siphalor.tweed4.tailor.coat;

import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;

@FunctionalInterface
public interface TweedCoatEntryProcessor<S> {
	/**
	 * Process a config value entry.
	 * @param parentWidget The parent Coat config widget that should be added to
	 * @param configEntry The config entry to process
	 * @param path The path to the config entry <i>including the name of the entry</i>
	 * @return Whether the processing should be treated as successful.
	 *         If <code>false</code> other processor will be tried for processing.
	 */
	boolean process(ConfigListWidget parentWidget, ValueConfigEntry<S> configEntry, String path);
}
