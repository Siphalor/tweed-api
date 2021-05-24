package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataValue;
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
