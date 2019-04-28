package de.siphalor.tweed.config;

/**
 * An enum which sets when a config can be (re-)loaded. <br><br>
 *
 * HIGHEST: guaranteed to be the highest scope (internal usage) <br>
 * GAME: reload when game starts <b><i><u>(triggered by manually calling {@link ConfigFile#triggerInitialLoad()})</u></i></b> <br>
 * WORLD: unused <br>
 * SMALLEST: always reload if some kind of reload is pushed
 */
public enum ConfigScope {
	HIGHEST(3),
	GAME(2),
	WORLD(1),
	SMALLEST(0);

	private final int value;

	ConfigScope(int value) {
		this.value = value;
	}

	public boolean triggeredBy(ConfigScope other) {
		return this.value <= other.value;
	}

	public boolean triggers(ConfigScope other) {
		return this.value >= other.value;
	}
}
