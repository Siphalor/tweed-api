package de.siphalor.tweed.config.entry;

import com.google.common.collect.HashBiMap;
import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonValue;

public class MappedEnumEntry<T extends Enum> extends AbstractValueEntry<T, MappedEnumEntry> {
	protected HashBiMap<String, T> stringToEnum;

	public MappedEnumEntry(T defaultValue) {
		super(defaultValue);
		stringToEnum = HashBiMap.create();
	}

	public AbstractValueEntry register(String name, T value) {
		stringToEnum.putIfAbsent(name, value);
		return this;
	}

	public AbstractValueEntry autoRegister(T[] values) {
		for(T value : values) {
			stringToEnum.putIfAbsent(value.name(), value);
		}
		return this;
	}

	@Override
	public T readValue(JsonValue json) {
		return getValue(json.asString());
	}

	@Override
	public void readValue(PacketByteBuf buf) {
		readValue(buf.readString());
	}

	public void readValue(String key) {
		value = getValue(key);
	}

	public T getValue(String string) {
		if(!stringToEnum.containsKey(string)) {
			return defaultValue;
		} else {
			return stringToEnum.get(string);
		}
	}

	@Override
	public JsonValue writeValue(T value) {
		return JsonValue.valueOf(stringToEnum.inverse().get(value));
	}

	@Override
	public void writeValue(PacketByteBuf buf) {
        buf.writeString(stringToEnum.inverse().get(value));
	}
}
