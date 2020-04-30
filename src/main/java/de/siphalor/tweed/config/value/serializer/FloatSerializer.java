package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class FloatSerializer extends ConfigValueSerializer<Float> {
	@Override
	public Float read(DataValue<?> data) {
		if(data.isNumber())
			return data.asFloat();
		return 0F;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Float value) {
		dataContainer.set(key, value);
	}

	@Override
	public Float read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Float value) {
		packetByteBuf.writeFloat(value);
	}

	@Override
	public String asString(Float value) {
		return value.toString();
	}

	@Override
	public Class<Float> getType() {
		return Float.class;
	}
}
