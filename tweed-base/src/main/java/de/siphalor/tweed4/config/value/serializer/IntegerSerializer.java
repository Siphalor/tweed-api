package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class IntegerSerializer extends ConfigValueSerializer<Integer> {
	@Override
	public Integer read(DataValue<?> data) {
		if (data.isNumber())
			return data.asInt();
		return 0;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Integer value) {
		dataContainer.set(key, value);
	}

	@Override
	public Integer read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Integer value) {
		packetByteBuf.writeInt(value);
	}

	@Override
	public String asString(Integer value) {
		return value.toString();
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}
}
