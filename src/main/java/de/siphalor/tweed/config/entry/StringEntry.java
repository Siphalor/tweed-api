package de.siphalor.tweed.config.entry;

import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonValue;

public class StringEntry extends AbstractValueEntry<String, StringEntry> {
	public StringEntry(String defaultValue) {
		super(defaultValue);
	}

	@Override
	public String readValue(JsonValue json) {
        return json.asString();
	}

	@Override
	public String readValue(PacketByteBuf buf) {
		return buf.readString();
	}

	@Override
	public JsonValue writeValue(String value) {
		return JsonValue.valueOf(value);
	}

	@Override
	public void writeValue(PacketByteBuf buf) {
		buf.writeString(value);
	}

}
