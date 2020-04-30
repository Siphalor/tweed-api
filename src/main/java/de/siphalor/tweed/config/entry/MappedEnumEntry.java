package de.siphalor.tweed.config.entry;

import com.google.common.collect.HashBiMap;
import de.siphalor.tweed.config.value.ConfigValue;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

@Deprecated
public class MappedEnumEntry<T extends Enum> extends AbstractValueEntry<T, MappedEnumEntry> {
	protected HashBiMap<String, T> stringToEnum;

	public MappedEnumEntry(T defaultValue) {
		super(defaultValue, ConfigValue.enumSerializer(defaultValue));
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
	public T readValue(DataValue<?> json) {
		return getValue(json.asString());
	}

	@Override
	public T readValue(PacketByteBuf buf) {
		return getValue(buf.readString(32767));
	}

	public T getValue(String string) {
		if(!stringToEnum.containsKey(string)) {
			return defaultValue;
		} else {
			return stringToEnum.get(string);
		}
	}

	public String getValue(T value) {
		if(!stringToEnum.inverse().containsKey(value)) {
			return stringToEnum.inverse().get(defaultValue);
		} else {
			return stringToEnum.inverse().get(value);
		}
	}

	@Override
	public <Key> void writeValue(DataContainer<?, Key> parent, Key name, T value) {
		parent.set(name, stringToEnum.inverse().get(value));
	}

	@Override
	public void writeValue(T value, PacketByteBuf buf) {
        buf.writeString(stringToEnum.inverse().get(value));
	}
}
