package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;

@SuppressWarnings("unchecked")
public abstract class AbstractBasicEntry<T extends AbstractBasicEntry> implements ConfigEntry {
	protected ConfigEnvironment environment = ConfigEnvironment.UNIVERSAL;
	protected ConfigScope scope = ConfigScope.SMALLEST;
	protected String comment = "";

	/**
	 * Sets the environment where this entry is defined
	 * @param environment the environment
	 * @return the current entry for chain calls
	 * @see ConfigEnvironment
	 */
	public T setEnvironment(ConfigEnvironment environment) {
		this.environment = environment;
		return (T) this;
	}

	@Override
	public ConfigEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * Sets the scope in which the config can be (re-)loaded
	 * @param scope the scope to use
	 * @return the current entry for chain calls
	 * @see ConfigScope
	 */
	public T setScope(ConfigScope scope) {
		this.scope = scope;
		return (T) this;
	}

	@Override
	public ConfigScope getScope() {
		return scope;
	}

	/**
	 * Sets the comment string which is written when the main config is exported.
	 * @param comment the comment to use
	 * @return the current entry for chain calls
	 */
	public T setComment(String comment) {
		this.comment = comment;
		return (T) this;
	}

	public String getComment() {
		return comment;
	}
}
