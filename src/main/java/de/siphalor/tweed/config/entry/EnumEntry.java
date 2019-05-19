package de.siphalor.tweed.config.entry;

import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonValue;

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
	public E readValue(JsonValue jsonValue) {
		for(E enumConstant : (E[]) defaultValue.getClass().getEnumConstants()) {
			if(enumConstant.toString().equalsIgnoreCase(jsonValue.asString()))
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
	public JsonValue writeValue(E value) {
		return JsonValue.valueOf(value.toString());
	}

	@Override
	public void writeValue(PacketByteBuf buf) {
		buf.writeVarInt(value.ordinal());
	}
}
