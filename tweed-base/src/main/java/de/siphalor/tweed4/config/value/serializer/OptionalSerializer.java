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
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;

import java.util.Optional;

/**
 * A serializer for {@link Optional}s
 * @since 1.3.0
 */
public class OptionalSerializer<T> extends ConfigValueSerializer<Optional<T>> {
	private final ConfigValueSerializer<T> valueSerializer;

	public OptionalSerializer(ConfigValueSerializer<T> valueSerializer) {
		this.valueSerializer = valueSerializer;
	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	Optional<T> read(V data) throws ConfigReadException {
		if (data.isNull()) {
			return Optional.empty();
		}
		return Optional.ofNullable(valueSerializer.read(data));
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, Optional<T> value) {
		if (value.isPresent()) {
			valueSerializer.write(dataContainer, key, value.get());
		} else {
			dataContainer.addNull(key);
		}
	}

	@Override
	public Optional<T> read(PacketByteBuf packetByteBuf) {
		if (packetByteBuf.readBoolean()) {
			return Optional.of(valueSerializer.read(packetByteBuf));
		}
		return Optional.empty();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Optional<T> value) {
		packetByteBuf.writeBoolean(value.isPresent());
		value.ifPresent(contained -> valueSerializer.write(packetByteBuf, contained));
	}

	@Override
	public Optional<T> copy(Optional<T> value) {
		return value.map(valueSerializer::copy);
	}

	@Override
	public String asString(Optional<T> value) {
		if (!value.isPresent()) {
			return "null";
		}
		return valueSerializer.asString(value.get());
	}

	@Override
	public Class<Optional<T>> getType() {
		//noinspection unchecked
		return ((Class<Optional<T>>)(Object) Optional.class);
	}
}
