package de.siphalor.tweed.config;

public enum ConfigEnvironment {
	CLIENT, UNIVERSAL, SERVER;

	public boolean matches(ConfigEnvironment other) {
		if(this.equals(other))
			return true;
		return other.equals(UNIVERSAL);
	}
}
