package de.siphalor.tweed.config.entry;

import org.hjson.JsonValue;

import java.util.HashMap;

public class MappedEnumConfig<T extends Enum> extends ConfigEntry<T> {
	protected HashMap<String, T> stringToEnum;

	public MappedEnumConfig(T defaultValue) {
		super(defaultValue);
	}

	public ConfigEntry register(String name, T value) {
		stringToEnum.putIfAbsent(name, value);
		return this;
	}

	public ConfigEntry autoRegister(T[] values) {
		for(T value : values) {
			stringToEnum.putIfAbsent(value.name(), value);
		}
		return this;
	}

	@Override
	public void read(JsonValue json) {
		if(!stringToEnum.containsKey(json.asString())) {
			value = defaultValue;
		} else {
			value = stringToEnum.get(json.asString());
		}
	}

	@Override
	public JsonValue write(T value) {
		return JsonValue.valueOf(value.toString());
	}
}
