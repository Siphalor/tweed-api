package de.siphalor.tweed.config;

/**
 * An enum which defines in which environment a config should be loaded.
 *
 * CLIENT: only clientside
 * SERVER: only serverside
 * SYNCED: configured at server side but synchronized to the clients
 * UNIVERSAL: on both sides
 * DEFAULT: only to be used internally
 */
public enum ConfigEnvironment {
	UNIVERSAL(null), CLIENT(UNIVERSAL), SERVER(UNIVERSAL), SYNCED(SERVER), DEFAULT(null);

	public final ConfigEnvironment parent;

	ConfigEnvironment(ConfigEnvironment parent) {
		this.parent = parent;
	}

	public boolean contains(ConfigEnvironment other) {
        while(other != null) {
        	if(this == other)
        		return true;
        	other = other.parent;
		}
        return false;
	}
}
