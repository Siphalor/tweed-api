package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class StringSerializer extends ConfigValueSerializer<String> {
	@Override
	public String read(DataValue<?> data) {
		if (data.isString()) {
			return data.asString();
		}
		return "";
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, String value) {
		dataContainer.set(key, value);
	}

	@Override
	public String read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, String value) {
		packetByteBuf.writeString(value);
	}

	@Override
	public String asString(String value) {
		return value;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}
}
