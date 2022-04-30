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

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.value.ConfigValue;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public abstract class AbstractValueConfigEntry<S, T> extends AbstractBasicEntry<S> {
	protected T defaultValue;
	protected Queue<Constraint<T>> constraints;
	protected Consumer<T> reloadListener;
	/**
	 * The value of this entry. Will be renamed when backwards compatibility is dropped.
	 */
	ConfigValue<T> currentValue;

	public AbstractValueConfigEntry(ConfigValue<T> configValue) {
		this.constraints = new ConcurrentLinkedQueue<>();
		this.currentValue = configValue;
	}

	public T getValue() {
		return currentValue.get();
	}

	public void setValue(T value) {
		this.currentValue.set(value);
	}

	/**
	 * Sets the default value. Use with care!
	 * @param defaultValue the new default value ("new default" lol)
	 */
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public abstract void setMainConfigValue(T mainConfigValue);

	public abstract T getMainConfigValue();

	@Deprecated
	public void setBothValues(T value) {
		setValue(value);
		setMainConfigValue(value);
	}

	public abstract Class<T> getType();

	public abstract String asString(T value);

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
		setValue(defaultValue);
		setMainConfigValue(defaultValue);
	}

	/**
	 * Register a constraint
	 *
	 * @param constraint the new constraint
	 * @return this entry for chain calls
	 */
	public final S addConstraint(Constraint<T> constraint) {
		constraints.add(constraint);
		//noinspection unchecked
		return (S) this;
	}

	public Queue<Constraint<T>> getConstraints() {
		return constraints;
	}

	@Override
	@NotNull
	public final Constraint.Result<T> applyConstraints() {
		Constraint.Result<T> result = applyConstraints(getValue());
		if (result.ok) {
			setValue(result.value);
		}
		return result;
	}

	public final Constraint.Result<T> applyConstraints(T value) {
		List<Pair<Constraint.Severity, String>> messages = new LinkedList<>();
		for (Constraint<T> constraint : constraints) {
			Constraint.Result<T> result = constraint.apply(value);
			messages.addAll(result.messages);
			if (!result.ok) {
				return new Constraint.Result<>(false, null, messages);
			}
			value = result.value;
		}
		return new Constraint.Result<>(true, value, messages);
	}

	public S setReloadListener(Consumer<T> listener) {
		this.reloadListener = listener;
		//noinspection unchecked
		return (S) this;
	}

	public void onReload() {
		if (reloadListener != null) {
			reloadListener.accept(getValue());
		}
	}

	@Override
	public String getDescription() {
		StringBuilder description = new StringBuilder();
		if(comment.length() > 0)
			description.append(getComment()).append(System.lineSeparator());
		description.append("default: ").append(asString(defaultValue)).append('\n');

		for (Constraint<T> constraint : constraints) {
			String constraintDesc = constraint.getDescription();
			if (constraintDesc.isEmpty()) {
				continue;
			}
			for (String line : StringUtils.split(constraintDesc, '\n')) {
				description.append('\t');
				description.append(line);
				description.append('\n');
			}
		}

		return description.toString().trim();
	}
}
