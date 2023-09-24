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
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerConfigurationTask;

import java.util.function.Consumer;

public class TweedConfigurationTask implements ServerPlayerConfigurationTask {
	public static final ServerPlayerConfigurationTask.Key KEY = new ServerPlayerConfigurationTask.Key(Tweed.MOD_ID + ":configuration_negotiation");

	@Override
	public void sendPacket(Consumer<Packet<?>> sender) {
		sender.accept(ServerConfigurationNetworking.createS2CPacket(new StartTweedConfigurationPacket()));
	}

	@Override
	public Key getKey() {
		return KEY;
	}
}
