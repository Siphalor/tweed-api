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
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

public class EnumSerializer<E extends Enum<?>> extends ConfigValueSerializer<E> {
	protected final E fallback;
	protected final E[] enumConstants;

	/**
	 * Use {@link ConfigSerializers#createEnum(Enum)} instead
	 */
	@ApiStatus.Internal
	public EnumSerializer(E fallback) {
		//noinspection unchecked
		this(fallback, (E[]) fallback.getClass().getEnumConstants());
	}

	protected EnumSerializer(E fallback, E[] enumConstants) {
		this.fallback = fallback;
		this.enumConstants = enumConstants;
	}

	@Override
	public <V> E read(DataSerializer<V> serializer, V value) throws ConfigReadException {
		try {
			String str = serializer.toString(value).toLowerCase(Locale.ROOT);
			for (E enumConstant : enumConstants) {
				if (enumConstant.name().toLowerCase(Locale.ROOT).equals(str)) {
					return enumConstant;
				}
			}
		} catch (Exception ignored) {}
		return fallback;
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, E value) {
		return value.name().toLowerCase(Locale.ROOT);
	}

	@Override
	public E read(PacketByteBuf packetByteBuf) {
		String str = packetByteBuf.readString(32767);
		for (E value : enumConstants) {
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
		//noinspection unchecked
		return (Class<E>) enumConstants[0].getClass();
	}
}
