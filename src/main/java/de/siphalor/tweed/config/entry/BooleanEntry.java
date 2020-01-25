package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

public class BooleanEntry extends AbstractValueEntry<Boolean, BooleanEntry> {
	public BooleanEntry(Boolean defaultValue) {
		super(defaultValue);
	}

	@Override
	public Boolean readValue(DataValue<?> dataValue) {
		return dataValue.asBoolean();
	}

	@Override
	public <Key> void writeValue(DataContainer<?, Key> parent, Key name, Boolean value) {
        parent.set(name, value);
	}

	@Override
	public Boolean readValue(PacketByteBuf buf) {
		return buf.readBoolean();
	}

	@Override
	public void writeValue(Boolean value, PacketByteBuf buf) {
		buf.writeBoolean(value);
	}
}
