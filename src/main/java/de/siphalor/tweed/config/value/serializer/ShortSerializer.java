package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class ShortSerializer extends ConfigValueSerializer<Short> {
	@Override
	public Short read(DataValue<?> data) throws ConfigReadException {
		return data.asShort();
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Short value) {
		dataContainer.set(key, value);
	}

	@Override
	public Short read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Short value) {
		packetByteBuf.writeShort(value);
	}

	@Override
	public String asString(Short value) {
		return value.toString();
	}

	@Override
	public Class<Short> getType() {
		return Short.class;
	}
}
