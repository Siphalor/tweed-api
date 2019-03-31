package de.siphalor.tweed.config;

import de.siphalor.tweed.config.entry.ConfigEntry;

public class ConfigCategory {
	protected String comment;
	protected ConfigFile owner;

	public ConfigCategory(ConfigFile owner) {
		comment = "";
		this.owner = owner;
	}

	/**
	 * Convenience method for adding entries to categories.
	 * @see ConfigFile#register(String, ConfigEntry)
	 */
	public <T extends ConfigEntry> T register(String name, T configEntry) {
		owner.register(name, configEntry);
		configEntry.setCategoryPath(owner.categories.inverse().get(this));
		return configEntry;
	}

	public ConfigCategory setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public String getComment() {
		return comment;
	}
}
