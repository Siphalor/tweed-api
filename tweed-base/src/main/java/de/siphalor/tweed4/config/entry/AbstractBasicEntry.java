package de.siphalor.tweed4.config.entry;

import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;

/**
 * An basic entry.
 * @param <T> The extending class
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBasicEntry<T> implements ConfigEntry<T> {
	protected ConfigEnvironment environment = ConfigEnvironment.DEFAULT;
	protected ConfigScope scope = ConfigScope.DEFAULT;
	protected String comment = "";

	@Override
	public T setEnvironment(ConfigEnvironment environment) {
		this.environment = environment;
		return (T) this;
	}

	@Override
	public ConfigEnvironment getEnvironment() {
		return environment;
	}

	@Override
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

	/**
	 * Gets the comment which might be used in descriptions
	 * @return
	 */
	public String getComment() {
		return comment;
	}
}
