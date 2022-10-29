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
import de.siphalor.tweed5.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed5.data.AnnotatedDataValue;
import de.siphalor.tweed5.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public class ConstantConfigEntry<T> extends AbstractBasicEntry<T> {
	private final T value;
	private final ConfigValueSerializer<T> valueSerializer;

	public ConstantConfigEntry(T value, ConfigValueSerializer<T> valueSerializer) {
		this.value = value;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public void reset(@NotNull ReloadEnvironment environment, @NotNull ReloadScope scope) {

	}

	@Override
	public <V> void read(@NotNull DataSerializer<V> serializer, @NotNull V value, @NotNull ReloadContext context) throws ConfigReadException {

	}

	@Override
	public void read(@NotNull PacketByteBuf buf, @NotNull ReloadContext context) {

	}

	@Override
	public void write(@NotNull PacketByteBuf buf, @NotNull ReloadContext context) {

	}

	@Override
	public <V> AnnotatedDataValue<Object> write(@NotNull DataSerializer<V> serializer, AnnotatedDataValue<V> oldValue, ReloadContext context) {
		return AnnotatedDataValue.of(valueSerializer.write(serializer, value));
	}

	@Override
	public String getDescription() {
		return getComment();
	}
}
