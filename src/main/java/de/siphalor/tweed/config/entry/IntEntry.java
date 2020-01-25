package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

@Deprecated
public class IntEntry extends AbstractValueEntry<Integer, IntEntry> {

	public IntEntry(Integer defaultValue) {
		super(defaultValue);
	}

	@Override
	public Integer readValue(DataValue<?> json) {
        return json.asInt();
	}

	@Override
	public Integer readValue(PacketByteBuf buf) {
		return buf.readVarInt();
	}

	@Override
	public <Key> void writeValue(DataContainer<?, Key> parent, Key name, Integer value) {
        parent.set(name, value);
	}

	@Override
	public void writeValue(Integer value, PacketByteBuf buf) {
		buf.writeVarInt(value);
	}
}
