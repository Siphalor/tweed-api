package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import de.siphalor.tweed.util.StaticStringConvertible;
import net.minecraft.util.PacketByteBuf;

public class StringConvertibleSerializer<T extends StaticStringConvertible<T>> extends ConfigValueSerializer<StaticStringConvertible<T>> {
	final T fallback;

	public StringConvertibleSerializer(T fallback) {
		this.fallback = fallback;
	}

	@Override
	public StaticStringConvertible<T> read(DataValue<?> data) throws ConfigReadException {
		if (data.isString()) {
			T val = fallback.valueOf(data.asString());
			return val == null ? fallback : val;
		}
		return fallback;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, StaticStringConvertible<T> value) {
		dataContainer.set(key, value.asString());
	}

	@Override
	public StaticStringConvertible<T> read(PacketByteBuf packetByteBuf) {
		return fallback.valueOf(packetByteBuf.readString(32767));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, StaticStringConvertible<T> value) {
		packetByteBuf.writeString(value.asString());
	}

	@Override
	public String asString(StaticStringConvertible<T> value) {
		return value.asString();
	}

	@Override
	public Class<StaticStringConvertible<T>> getType() {
		return (Class<StaticStringConvertible<T>>) fallback.getClass();
	}
}
