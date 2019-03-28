package de.siphalor.tweed.config;

public enum ConfigDefinitionScope {
	GAME(2),
	@Deprecated WORLD(1),
	NONE(0);

	private final int value;

	ConfigDefinitionScope(int value) {
		this.value = value;
	}

	public boolean isIn(ConfigDefinitionScope other) {
		return this.value <= other.value;
	}
}
