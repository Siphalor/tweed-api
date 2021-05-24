package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class BooleanSerializer extends ConfigValueSerializer<Boolean> {
	@Override
	public Boolean read(DataValue<?> data) {
		if (data.isBoolean())
			return data.asBoolean();
		return false;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Boolean value) {
		dataContainer.set(key, value);
	}

	@Override
	public Boolean read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Boolean value) {
		packetByteBuf.writeBoolean(value);
	}

	@Override
	public String asString(Boolean value) {
		return value.toString();
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
