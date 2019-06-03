package de.siphalor.tweed.config.entry;

import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonValue;

public class BooleanEntry extends AbstractValueEntry<Boolean, BooleanEntry> {
	public BooleanEntry(Boolean defaultValue) {
		super(defaultValue);
	}

	@Override
	public Boolean readValue(JsonValue json) {
		return json.asBoolean();
	}

	@Override
	public JsonValue writeValue(Boolean value) {
		return JsonValue.valueOf(value);
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
