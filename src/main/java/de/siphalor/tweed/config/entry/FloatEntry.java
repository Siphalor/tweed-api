package de.siphalor.tweed.config.entry;

import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonValue;

public class FloatEntry extends AbstractValueEntry<Float, FloatEntry> {
	public FloatEntry(Float defaultValue) {
		super(defaultValue);
	}

	@Override
	public Float readValue(JsonValue json) {
		return json.asFloat();
	}

	@Override
	public void readValue(PacketByteBuf buf) {
		value = buf.readFloat();
	}

	@Override
	public JsonValue writeValue(Float value) {
		return JsonValue.valueOf(value);
	}

	@Override
	public void writeValue(PacketByteBuf buf) {
		buf.writeFloat(value);
	}
}
