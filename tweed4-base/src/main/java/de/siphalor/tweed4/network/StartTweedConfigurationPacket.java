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

package de.siphalor.tweed4.network;

import de.siphalor.tweed4.Tweed;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class StartTweedConfigurationPacket implements FabricPacket {
	public static final PacketType<StartTweedConfigurationPacket> TYPE = PacketType.create(
			new Identifier(Tweed.MOD_ID, "config_query"),
			StartTweedConfigurationPacket::read
	);

	public static StartTweedConfigurationPacket read(PacketByteBuf buf) {
		buf.readBoolean();
		return new StartTweedConfigurationPacket();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(false);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}
}
