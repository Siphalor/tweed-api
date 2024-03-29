/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.config.entry;

import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;
import org.jetbrains.annotations.ApiStatus;

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

	/**
	 * @inheritDoc
	 */
	@Override
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.0")
	public ConfigEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public final ConfigEnvironment getOwnEnvironment() {
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
	@Override
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
