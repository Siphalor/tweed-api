package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

public abstract class ConfigValueSerializer<V> {
	public abstract V read(DataValue<?> data) throws ConfigReadException;
	public abstract <Key> void write(DataContainer<?, Key> dataContainer, Key key, V value);

	public abstract V read(PacketByteBuf packetByteBuf);
	public abstract void write(PacketByteBuf packetByteBuf, V value);

	public abstract String asString(V value);

	public abstract Class<V> getType();
}
