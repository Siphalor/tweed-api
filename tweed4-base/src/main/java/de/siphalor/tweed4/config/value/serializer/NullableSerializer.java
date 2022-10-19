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

package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataNull;
import de.siphalor.tweed4.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;

/**
 * A serializer wrapper for nullable values
 * @since 1.3.0
 */
public class NullableSerializer<T> extends ConfigValueSerializer<T> {
	private final ConfigValueSerializer<T> valueSerializer;

	public NullableSerializer(ConfigValueSerializer<T> valueSerializer) {
		this.valueSerializer = valueSerializer;
	}

	@Override
	public <V> T read(DataSerializer<V> serializer, V value) throws ConfigReadException {
		if (value == DataNull.INSTANCE) {
			return null;
		} else {
			return valueSerializer.read(serializer, value);
		}
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, T value) {
		if (value == null) {
			return DataNull.INSTANCE;
		} else {
			return valueSerializer.write(serializer, value);
		}
	}

	@Override
	public T read(PacketByteBuf packetByteBuf) {
		if (packetByteBuf.readBoolean()) {
			return valueSerializer.read(packetByteBuf);
		} else {
			return null;
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, T value) {
		if (value != null) {
			packetByteBuf.writeBoolean(true);
			valueSerializer.write(packetByteBuf, value);
		} else {
			packetByteBuf.writeBoolean(false);
		}
	}

	@Override
	public T copy(T value) {
		if (value != null) {
			return valueSerializer.copy(value);
		}
		return null;
	}

	@Override
	public String asString(T value) {
		if (value == null) {
			return "null";
		}
		return valueSerializer.asString(value);
	}

	@Override
	public Class<T> getType() {
		return valueSerializer.getType();
	}
}
