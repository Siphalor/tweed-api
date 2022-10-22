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

package de.siphalor.tweed5.config.value.serializer;

import de.siphalor.tweed5.config.ConfigReadException;
import de.siphalor.tweed5.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;

public class DoubleSerializer extends ConfigValueSerializer<Double> {
	@Override
	public <V> Double read(DataSerializer<V> serializer, V value) throws ConfigReadException {
		return serializer.toDouble(value);
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, Double value) {
		return value;
	}

	@Override
	public Double read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readDouble();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Double value) {
		packetByteBuf.writeDouble(value);
	}

	@Override
	public String asString(Double value) {
		return value.toString();
	}

	@Override
	public Class<Double> getType() {
		return Double.class;
	}
}
