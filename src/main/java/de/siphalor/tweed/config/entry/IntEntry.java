package de.siphalor.tweed.config.entry;

import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonValue;

public class IntEntry extends AbstractValueEntry<Integer, IntEntry> {

	public IntEntry(Integer defaultValue) {
		super(defaultValue);
	}

	@Override
	public Integer readValue(JsonValue json) {
        return json.asInt();
	}

	@Override
	public Integer readValue(PacketByteBuf buf) {
		return buf.readVarInt();
	}

	@Override
	public JsonValue writeValue(Integer value) {
		return JsonValue.valueOf(value);
	}

	@Override
	public void writeValue(PacketByteBuf buf) {
		buf.writeVarInt(value);
	}
}
