package de.siphalor.tweed.config.entry;

import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonValue;

public class IntEntry extends AbstractValueEntry<Integer, IntEntry> {

	public IntEntry(Integer defaultValue) {
		super(defaultValue);
	}

	@Override
	public void readValue(JsonValue json) {
		if(json.isNumber()){
			value = json.asInt();
		}
	}

	@Override
	public void readValue(PacketByteBuf buf) {
		value = buf.readVarInt();
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
