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

import de.siphalor.tweed4.data.DataSerializer;
import net.minecraft.network.PacketByteBuf;

public class BooleanSerializer extends ConfigValueSerializer<Boolean> {
	@Override
	public <V> Boolean read(DataSerializer<V> serializer, V value) {
		if (value instanceof Boolean)
			return ((Boolean) value);
		return false;
	}

	@Override
	public <V> Object write(DataSerializer<V> serializer, Boolean value) {
		return value;
	}

	@Override
	public Boolean read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Boolean value) {
		packetByteBuf.writeBoolean(value);
	}

	@Override
	public String asString(Boolean value) {
		return value.toString();
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
