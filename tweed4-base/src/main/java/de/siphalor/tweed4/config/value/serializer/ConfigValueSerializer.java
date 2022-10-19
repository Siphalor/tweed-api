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
import de.siphalor.tweed4.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;

public abstract class ConfigValueSerializer<T> {
	public abstract <V> T read(DataSerializer<V> serializer, V value) throws ConfigReadException;

	public abstract <V> Object write(DataSerializer<V> serializer, T value);

	public abstract T read(PacketByteBuf packetByteBuf);
	public abstract void write(PacketByteBuf packetByteBuf, T value);

	public T copy(T value) {
		return value;
	}

	public abstract String asString(T value);

	public abstract Class<T> getType();
}
