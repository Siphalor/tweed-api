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
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
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
	public <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	E read(V data) {
		if (data.isString()) {
			String str = data.asString().toLowerCase(Locale.ENGLISH);
			for (E value : enumConstants) {
				if (value.name().toLowerCase(Locale.ENGLISH).equals(str)) {
					return value;
				}
			}
		}
		return fallback;
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, E value) {
		dataContainer.set(key, value.name());
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
