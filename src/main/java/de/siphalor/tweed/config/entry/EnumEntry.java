package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

@SuppressWarnings("unchecked")
public class EnumEntry<E extends Enum<?>> extends AbstractValueEntry<E, EnumEntry> {
	/**
	 * Constructs a new entry
	 *
	 * @param defaultValue The default value to use
	 */
	public EnumEntry(E defaultValue) {
		super(defaultValue);
	}

	@Override
	public E readValue(DataValue<?> dataValue) {
		for(E enumConstant : (E[]) defaultValue.getClass().getEnumConstants()) {
			if(enumConstant.toString().equalsIgnoreCase(dataValue.asString()))
				return enumConstant;
		}
		return defaultValue;
	}

	@Override
	public E readValue(PacketByteBuf buf) {
		final int ordinal = buf.readVarInt();
		for(E enumConstant : (E[]) defaultValue.getClass().getEnumConstants()) {
			if(enumConstant.ordinal() == ordinal)
				return enumConstant;
		}
        return defaultValue;
	}

	@Override
	public <Key> void writeValue(DataContainer<?, Key> parent, Key name, E value) {
		parent.set(name, value.toString());
	}

	@Override
	public void writeValue(E value, PacketByteBuf buf) {
		buf.writeVarInt(value.ordinal());
	}
}
