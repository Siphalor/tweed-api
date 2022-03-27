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
import de.siphalor.tweed4.config.ConfigOrigin;
import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Interface for all config entries
 * @param <T> The implementing class
 */
public interface ConfigEntry<T> {

	/**
	 * Resets this entry to its default
	 * @param environment the current environment the current environment (handled by the system, can be ignored in most cases)
	 * @param scope the current scope the current scope (handled by the system, can be ignored in most cases)
	 */
	void reset(ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Abstract method for reading the entry's value from a data object
	 * @param dataValue the given data value
	 * @param environment the current environment
	 * @param scope the current reload scope
	 * @throws ConfigReadException if an issue occurs during reading the value
	 */
	<V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void read(V dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException;

	/**
	 * Read this kind of entry from a packet.
	 * @param buf the packet's buffer
	 * @param environment the current environment
	 * @param scope the current reload scope
	 * @param origin the kind of source where this data comes from/should go to
	 */
	void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin);

	/**
	 * Write this kind of entry to a packet.
	 * @param buf the packet's buffer
	 * @param environment the current environment
	 * @param scope the current reload scope
	 * @param origin the kind of source where this data comes from/should go to
	 */
	void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin);

	/**
	 * Method to write the main config value of the entry to HJSON (to be read by the user).
	 * @param dataContainer the object where this entry should be appended to
	 * @param key the key under which this entry should be appended
	 * @param environment the current environment (handled by the system, can be ignored in most cases)
	 * @param scope the current scope (handled by the system, can be ignored in most cases)
	 */
	<Key, V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope);

	/**
	 * Sets the environment where this entry is defined
	 * @param environment the environment
	 * @return the current entry for chain calls
	 * @see ConfigEnvironment
	 */
	T setEnvironment(ConfigEnvironment environment);

	/**
	 * Gets the environment where this entry will be loaded in.
	 * This will be used to decide whether this entry's methods should be called or not.
	 * If the entry is a composite entry, this is usually the combined environment of all its children.
	 * @return the environment
	 */
	ConfigEnvironment getEnvironment();

	/**
	 * Gets the actual environment of the entry itself.
	 * For composite entries this must always deliver the internal environment of the entry,
	 * which is usually {@link ConfigEnvironment#DEFAULT} by default.
	 * @return the environment of the entry itself
	 */
	@ApiStatus.Internal
	@ApiStatus.OverrideOnly
	default ConfigEnvironment getOwnEnvironment() {
		return getEnvironment();
	}

	/**
	 * Sets the scope in which the config can be (re-)loaded
	 * @param scope the scope to use
	 * @return the current entry for chain calls
	 * @see ConfigScope
	 */
	T setScope(ConfigScope scope);

	/**
	 * Gets the scope in which the entry gets reloaded.
	 * @return the scope
	 */
	ConfigScope getScope();

	/**
	 * May set the comment string that describes the entry to the user.
	 * @param comment the comment to use
	 * @return the current entry for chain calls
	 */
	T setComment(String comment);

	/**
	 * Gives a description for what this config entry triggeredBy, possible constraints etc.
	 * @return the description
	 */
	String getDescription();

	default Optional<Text[]> getClothyDescription() {
		List<LiteralText> list = new ArrayList<>();
		for (String s : getDescription().replace("\t", "    ").split("[\n\r]\r?")) {
			LiteralText literalText = new LiteralText(s);
			list.add(literalText);
		}
		if (list.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(list.toArray(new Text[0]));
		}
	}

	/**
	 * Method for handling possible constraints after reading in the value.
	 */
	@Nullable
	default Constraint.Result<?> applyConstraints() {
		return Constraint.Result.OK;
	}
}
