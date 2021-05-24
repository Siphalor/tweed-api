package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class LongSerializer extends ConfigValueSerializer<Long> {
	@Override
	public Long read(DataValue<?> data) throws ConfigReadException {
		return data.asLong();
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Long value) {
		dataContainer.set(key, value);
	}

	@Override
	public Long read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Long value) {
		packetByteBuf.writeLong(value);
	}

	@Override
	public String asString(Long value) {
		return value.toString();
	}

	@Override
	public Class<Long> getType() {
		return Long.class;
	}
}
