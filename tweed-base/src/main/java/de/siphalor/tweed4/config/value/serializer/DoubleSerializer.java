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
import net.minecraft.util.PacketByteBuf;

public class DoubleSerializer extends ConfigValueSerializer<Double> {
	@Override
	public Double read(DataValue<?> data) throws ConfigReadException {
		return data.asDouble();
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Double value) {
		dataContainer.set(key, value);
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
