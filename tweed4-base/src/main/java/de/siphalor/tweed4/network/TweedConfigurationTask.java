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
