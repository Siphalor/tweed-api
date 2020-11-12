package de.siphalor.tweed.config;

/**
 * An enum which sets when a config can be (re-)loaded. <br><br>
 *
 * HIGHEST: guaranteed to be the highest scope (internal usage) <br>
 * GAME: reload when game starts <br>
 * WORLD: unused <br>
 * SMALLEST: always reload if some kind of reload is pushed <br>
 * DEFAULT: <i>should only be used internally</i>
 */
public enum ConfigScope {
	HIGHEST(3),
	GAME(2),
	WORLD(1),
	SMALLEST(0),
	DEFAULT(-1);

	private final int value;

	ConfigScope(int value) {
		this.value = value;
	}

	@Deprecated
	public boolean triggeredBy(ConfigScope other) {
		return this.value <= other.value;
	}

	public boolean triggers(ConfigScope other) {
		return this.value >= other.value;
	}
}
