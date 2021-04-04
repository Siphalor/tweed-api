package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public class CharacterSerializer extends ConfigValueSerializer<Character> {
	@Override
	public Character read(DataValue<?> data) throws ConfigReadException {
		return data.asChar();
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, Character value) {
		dataContainer.set(key, value);
	}

	@Override
	public Character read(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readChar();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, Character value) {
		packetByteBuf.writeChar(value);
	}

	@Override
	public String asString(Character value) {
		return String.valueOf(value);
	}

	@Override
	public Class<Character> getType() {
		return Character.class;
	}
}
