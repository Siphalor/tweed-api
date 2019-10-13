package de.siphalor.tweed.modules.api.features;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.config.entry.BooleanEntry;

public abstract class OptionalFeature extends Feature {
	protected BooleanEntry enabledEntry;
	boolean applied = false;
	public String name;

	public OptionalFeature(String name, String enablesDescription) {
		this.name = name;
		enabledEntry = register(name, createEnabledEntry(enablesDescription));
		enabledEntry.setReloadListener(this::onReload);
	}

	protected BooleanEntry createEnabledEntry(String description) {
		return new BooleanEntry(true).setComment(description).setScope(ConfigScope.GAME).setEnvironment(ConfigEnvironment.SERVER);
	}

	public OptionalFeature disableByDefault() {
		enabledEntry.setDefaultValue(false);
		return this;
	}

	public boolean isEnabled() {
		return enabledEntry.value;
	}

	public final void onReload(boolean enabled) {
		if(enabled) {
			apply();
			if (!applied) applyOnce();
			applied = true;
		}
	}

	public void apply() {

	}

	public void applyOnce() {

	}
}
