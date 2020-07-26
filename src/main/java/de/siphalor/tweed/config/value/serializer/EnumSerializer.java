package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import java.util.Locale;
import net.minecraft.util.PacketByteBuf;

public class EnumSerializer<E extends Enum<?>> extends ConfigValueSerializer<E> {
	E fallback;

	public EnumSerializer(E fallback) {
		this.fallback = fallback;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E read(DataValue<?> data) {
		if (data.isString()) {
			String str = data.asString().toLowerCase(Locale.ENGLISH);
			for (E value : (E[]) fallback.getClass().getEnumConstants()) {
				if (value.name().toLowerCase(Locale.ENGLISH).equals(str)) {
					return value;
				}
			}
		}
		return fallback;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, E value) {
		dataContainer.set(key, value.name());
	}

	@SuppressWarnings("unchecked")
	@Override
	public E read(PacketByteBuf packetByteBuf) {
		String str = packetByteBuf.readString(32767);
		for (E value : (E[]) fallback.getClass().getEnumConstants()) {
			if (value.name().toLowerCase(Locale.ENGLISH).equals(str)) {
				return value;
			}
		}
		return fallback;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, E value) {
		packetByteBuf.writeString(value.name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public String asString(E value) {
		return value.name();
	}

	@Override
	public Class<E> getType() {
		return (Class<E>) fallback.getClass();
	}
}
