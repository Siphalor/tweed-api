package de.siphalor.tweed4.network;

import de.siphalor.tweed4.Tweed;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RequestSyncPacket implements FabricPacket {
	public static final PacketType<RequestSyncPacket> TYPE = PacketType.create(
			new Identifier(Tweed.MOD_ID, "request_sync"),
			RequestSyncPacket::read
	);

	private final List<String> requestedFiles;

	public RequestSyncPacket(List<String> requestedFiles) {
		this.requestedFiles = requestedFiles;
	}

	public static RequestSyncPacket read(PacketByteBuf buf) {
		int size = buf.readVarInt();
		List<String> requests = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			requests.add(buf.readString(32767));
		}
		return new RequestSyncPacket(requests);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(requestedFiles.size());
		for (String request : requestedFiles) {
			buf.writeString(request);
		}
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}

	public List<String> getRequestedFiles() {
		return requestedFiles;
	}
}
