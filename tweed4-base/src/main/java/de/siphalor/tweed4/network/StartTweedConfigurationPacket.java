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
