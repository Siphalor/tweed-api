package de.siphalor.tweed.config;

/**
 * An enum which defines in which environment a config should be loaded.
 *
 * CLIENT: only clientside
 * SERVER: only serverside
 * SYNCED: configured at server side but synchronized to the clients
 * UNIVERSAL: on both sides
 */
public enum ConfigEnvironment {
	CLIENT(0), UNIVERSAL(1), SERVER(2), SYNCED(2);

	private final int containmentId;

	ConfigEnvironment(int containmentId) {
		this.containmentId = containmentId;
	}

	/**
	 * Returns whether the current environment is valid in the other environment
	 * @param other the other environment
	 * @return a boolean denoting it
	 */
	public boolean isContainedIn(ConfigEnvironment other) {
		if(this.containmentId == other.containmentId)
			return true;
		return other.equals(UNIVERSAL);
	}

	public boolean contains(ConfigEnvironment other) {
		if(this.containmentId == other.containmentId)
			return true;
		return this.equals(UNIVERSAL);
	}
}
