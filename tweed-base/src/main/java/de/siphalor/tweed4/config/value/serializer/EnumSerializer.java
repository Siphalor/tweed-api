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

import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.Locale;

public class EnumSerializer<E extends Enum<?>> extends ConfigValueSerializer<E> {
	E fallback;

	public EnumSerializer(E fallback) {
		this.fallback = fallback;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E read(DataValue<?> data) {
		if (data.isString()) {
			String str = data.asString().toLowerCase(Locale.ENGLISH);
			for (E value : (E[]) fallback.getClass().getEnumConstants()) {
				if (value.name().toLowerCase(Locale.ENGLISH).equals(str)) {
					return value;
				}
			}
		}
		return fallback;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, E value) {
		dataContainer.set(key, value.name());
	}

	@SuppressWarnings("unchecked")
	@Override
	public E read(PacketByteBuf packetByteBuf) {
		String str = packetByteBuf.readString(32767);
		for (E value : (E[]) fallback.getClass().getEnumConstants()) {
			if (value.name().toLowerCase(Locale.ENGLISH).equals(str)) {
				return value;
			}
		}
		return fallback;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, E value) {
		packetByteBuf.writeString(value.name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public String asString(E value) {
		return value.name();
	}

	@Override
	public Class<E> getType() {
		return (Class<E>) fallback.getClass();
	}
}
