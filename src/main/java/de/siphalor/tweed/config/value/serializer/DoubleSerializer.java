package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

public class DoubleSerializer extends ConfigValueSerializer<Double> {
	@Override
	public Double read(DataValue<?> data) throws ConfigReadException {
		return data.asDouble();
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Double value) {
		dataContainer.set(key, value);
	}

	@Override
	public Double read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readDouble();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Double value) {
		packetByteBuf.writeDouble(value);
	}

	@Override
	public String asString(Double value) {
		return value.toString();
	}

	@Override
	public Class<Double> getType() {
		return Double.class;
	}
}
