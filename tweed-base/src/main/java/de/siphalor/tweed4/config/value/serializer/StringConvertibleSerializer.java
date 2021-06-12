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

package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataValue;
import de.siphalor.tweed4.util.StaticStringConvertible;
import net.minecraft.network.PacketByteBuf;

public class StringConvertibleSerializer<T extends StaticStringConvertible<T>> extends ConfigValueSerializer<StaticStringConvertible<T>> {
	final T fallback;

	public StringConvertibleSerializer(T fallback) {
		this.fallback = fallback;
	}

	@Override
	public StaticStringConvertible<T> read(DataValue<?> data) throws ConfigReadException {
		if (data.isString()) {
			T val = fallback.valueOf(data.asString());
			return val == null ? fallback : val;
		}
		return fallback;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, StaticStringConvertible<T> value) {
		dataContainer.set(key, value.asString());
	}

	@Override
	public StaticStringConvertible<T> read(PacketByteBuf packetByteBuf) {
		return fallback.valueOf(packetByteBuf.readString(32767));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, StaticStringConvertible<T> value) {
		packetByteBuf.writeString(value.asString());
	}

	@Override
	public String asString(StaticStringConvertible<T> value) {
		return value.asString();
	}

	@Override
	public Class<StaticStringConvertible<T>> getType() {
		return (Class<StaticStringConvertible<T>>) fallback.getClass();
	}
}
