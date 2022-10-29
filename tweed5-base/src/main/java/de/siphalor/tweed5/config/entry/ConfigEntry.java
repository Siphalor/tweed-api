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

package de.siphalor.tweed5.config.entry;

import de.siphalor.tweed5.reload.ReloadContext;
import de.siphalor.tweed5.reload.ReloadEnvironment;
import de.siphalor.tweed5.config.ConfigReadException;
import de.siphalor.tweed5.reload.ReloadScope;
import de.siphalor.tweed5.config.constraints.Constraint;
import de.siphalor.tweed5.data.AnnotatedDataValue;
import de.siphalor.tweed5.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for all config entries
 * @param <T> The implementing class
 */
@ParametersAreNonnullByDefault
public interface ConfigEntry<T> {

	/**
	 * Resets this entry to its default
	 * @param environment the current environment the current environment (handled by the system, can be ignored in most cases)
	 * @param scope the current scope the current scope (handled by the system, can be ignored in most cases)
	 */
	void reset(ReloadEnvironment environment, ReloadScope scope);

	/**
	 * Abstract method for reading the entry's value from a data object
	 *
	 * @param serializer the serializer used for the data
	 * @param value      the given data value
	 * @param context    the current reload context
	 * @throws ConfigReadException if an issue occurs during reading the value
	 */
	<V> void read(DataSerializer<V> serializer, V value, ReloadContext context) throws ConfigReadException;

	/**
	 * Read this kind of entry from a packet.
	 *
	 * @param buf     the packet's buffer
	 * @param context the current reload context
	 */
	void read(PacketByteBuf buf, ReloadContext context);

	/**
	 * Write this kind of entry to a packet.
	 *
	 * @param buf     the packet's buffer
	 * @param context the current reload context
	 */
	void write(PacketByteBuf buf, ReloadContext context);

	/**
	 * Method to write the main config value of the entry to a serialized form.
	 * Use {@link DataSerializer#fromRaw(Object)} to convert primitive values or use {@link DataSerializer#newObject()}/{@link DataSerializer#newList()} for maps and lists.
	 *
	 * @param serializer the serializer used for the data
	 * @param oldValue   the old value of the entry
	 * @param context    the current reload context
	 * @return The value serialized as annotated data
	 */
	<V> AnnotatedDataValue<Object> write(DataSerializer<V> serializer, @Nullable AnnotatedDataValue<V> oldValue, ReloadContext context);

	/**
	 * Sets the environment where this entry is defined
	 * @param environment the environment
	 * @return the current entry for chain calls
	 * @see ReloadEnvironment
	 */
	T setEnvironment(ReloadEnvironment environment);

	/**
	 * Gets the actual environment of the entry itself.
	 * For composite entries this must always deliver the internal environment of the entry,
	 * which is usually {@link ReloadEnvironment#UNSPECIFIED} by default.
	 * @return the environment of the entry itself
	 */
	ReloadEnvironment getOwnEnvironment();

	/**
	 * Sets the scope in which the config can be (re-)loaded
	 * @param scope the scope to use
	 * @return the current entry for chain calls
	 * @see ReloadScope
	 */
	T setScope(ReloadScope scope);

	/**
	 * Gets the scope in which the entry gets reloaded.
	 * @return the scope
	 */
	ReloadScope getScope();

	/**
	 * Checks whether this entry is triggered by the given context.
	 * Convenience method for {@link #matches(ReloadEnvironment, ReloadScope)}.
	 * @param context the context to check
	 * @return whether the entry is triggered by the context
	 */
	default boolean matches(@NotNull ReloadContext context) {
		return matches(context.getEnvironment(), context.getScope());
	}

	/**
	 * Checks whether this entry is triggered by the given environment and scope.
	 * This should be overridden by composite entries to check whether any entry is triggered by the given environment and scope.
	 * @param environment the environment to check
	 * @param scope the scope to check
	 * @return whether the entry is triggered by the given environment and scope
	 */
	default boolean matches(@Nullable ReloadEnvironment environment, @Nullable ReloadScope scope) {
		return (environment == null || environment.triggers(getOwnEnvironment()))
				&& (scope == null || scope.triggers(getScope()));
	}

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

	/**
	 * Method for handling possible constraints after reading in the value.
	 */
	@NotNull
	default Constraint.Result<?> applyConstraints() {
		return Constraint.Result.OK;
	}
}
