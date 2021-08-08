/*
 * Copyright 2021 Siphalor
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
import de.siphalor.tweed4.config.*;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.value.ConfigValue;
import de.siphalor.tweed4.config.value.SimpleConfigValue;
import de.siphalor.tweed4.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An entry to register at a {@link ConfigFile} or {@link ConfigCategory}.
 * @param <T> the type which is used for maintaining the value of the entry. Use {@link ValueConfigEntry#currentValue} to access
 */
@SuppressWarnings("unchecked")
public class ValueConfigEntry<T> extends AbstractBasicEntry<ValueConfigEntry<T>> {
	private ConfigValueSerializer<T> valueSerializer;

	/**
	 * The value of this entry. Will be renamed when backwards compatibility is dropped.
	 */
	ConfigValue<T> currentValue;

	protected T mainConfigValue;

	protected T defaultValue;
	protected Queue<Constraint<T>> constraints;

	protected Consumer<T> reloadListener;

	/**
	 * Constructs a new entry
	 * @param defaultValue The default value to use
	 */
	public ValueConfigEntry(T defaultValue) {
		this(new SimpleConfigValue<>(defaultValue), (ConfigValueSerializer<T>) ConfigValue.serializer(defaultValue, defaultValue.getClass()));
	}

	public ValueConfigEntry(T defaultValue, ConfigValueSerializer<T> configValueSerializer) {
		this(new SimpleConfigValue<>(defaultValue), configValueSerializer);
	}

	public ValueConfigEntry(ConfigValue<T> configValue, ConfigValueSerializer<T> valueSerializer) {
		this.valueSerializer = valueSerializer;
		this.currentValue = configValue;
		this.defaultValue = currentValue.get();
		this.mainConfigValue = defaultValue;
		this.comment = "";
		this.environment = ConfigEnvironment.UNIVERSAL;
		this.constraints = new ConcurrentLinkedQueue<>();
	}

	public ConfigValueSerializer<T> getValueSerializer() {
		return valueSerializer;
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

	public void setMainConfigValue(T mainConfigValue) {
		this.mainConfigValue = mainConfigValue;
	}

	public final T getMainConfigValue() {
		return mainConfigValue;
	}

	@Deprecated
	public void setBothValues(T value) {
		this.currentValue.set(value);
		this.mainConfigValue = value;
	}

	public Class<T> getType() {
		return valueSerializer.getType();
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
		currentValue.set(defaultValue);
		mainConfigValue = defaultValue;
	}

	/**
	 * Register a constraint
	 * @param constraint the new constraint
	 * @return this entry for chain calls
	 */
	public final ValueConfigEntry<T> addConstraint(Constraint<T> constraint) {
        constraints.add(constraint);
    	return this;
    }

	public Queue<Constraint<T>> getConstraints() {
		return constraints;
	}

	@Override
	public final Constraint.Result<T> applyConstraints() {
		Constraint.Result<T> result = applyConstraints(getValue());
		if (result.ok) {
			setValue(result.value);
		}
		return result;
	}

	public final Constraint.Result<T> applyConstraints(T value) {
		List<Pair<Constraint.Severity, String>> messages = new LinkedList<>();
		for(Constraint<T> constraint : constraints) {
			Constraint.Result<T> result = constraint.apply(value);
			messages.addAll(result.messages);
			if (!result.ok) {
				return new Constraint.Result<>(false, null, messages);
			}
			value = result.value;
		}
		return new Constraint.Result<>(true, value, messages);
    }

	@Override
	public String getDescription() {
		StringBuilder description = new StringBuilder();
		if(comment.length() > 0)
			description.append(getComment()).append(System.lineSeparator());
		description.append("default: ").append(valueSerializer != null ? valueSerializer.asString(defaultValue) : defaultValue.toString());

		String constraintDesc = constraints.stream().flatMap(constraint -> {
			String desc = constraint.getDescription();
			if (desc.isEmpty())
				return Stream.empty();
			return Arrays.stream(desc.split("\n"));
		}).collect(Collectors.joining(System.lineSeparator() + "\t"));
		if (!constraintDesc.isEmpty()) {
			description.append('\n').append(constraintDesc);
		}

		return description.toString();
	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void read(V dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		currentValue.set(valueSerializer.read(dataValue));
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = currentValue.get();
		}
		onReload();
	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(environment.triggers(getEnvironment())) {
			if(scope.triggers(getScope())) {
				if(origin == ConfigOrigin.MAIN)
					mainConfigValue = valueSerializer.read(buf);
				else
					currentValue.set(valueSerializer.read(buf));
				onReload();
			}
			else if(origin == ConfigOrigin.MAIN)
				mainConfigValue = valueSerializer.read(buf);
		} else {
			valueSerializer.read(buf);
		}
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> void write(DataContainer<Key, V, L, O> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		valueSerializer.write(dataContainer, key, mainConfigValue);
        if(dataContainer.has(key)) dataContainer.get(key).setComment(getDescription());
    }

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(origin == ConfigOrigin.MAIN)
			valueSerializer.write(buf, mainConfigValue);
		else
			valueSerializer.write(buf, currentValue.get());
	}

	public ValueConfigEntry<T> setReloadListener(Consumer<T> listener) {
		this.reloadListener = listener;
		return this;
	}

    public void onReload() {
		if(reloadListener != null)
			reloadListener.accept(currentValue.get());
	}
}
