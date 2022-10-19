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

public class ByteSerializer extends ConfigValueSerializer<Byte> {
	@Override
	public <V> Byte read(DataSerializer<V> serializer, V value) throws ConfigReadException {
		return serializer.toByte(value);
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, Byte value) {
		return serializer.fromRaw(value);
	}

	@Override
	public Byte read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Byte value) {
		packetByteBuf.writeByte(value);
	}

	@Override
	public String asString(Byte value) {
		return value.toString();
	}

	@Override
	public Class<Byte> getType() {
		return Byte.class;
	}
}
