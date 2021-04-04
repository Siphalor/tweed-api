package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class ByteSerializer extends ConfigValueSerializer<Byte> {
	@Override
	public Byte read(DataValue<?> data) throws ConfigReadException {
		return data.asByte();
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Byte value) {
		dataContainer.set(key, value);
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
