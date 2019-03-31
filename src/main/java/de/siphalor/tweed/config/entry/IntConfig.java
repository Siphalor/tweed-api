package de.siphalor.tweed.config.entry;

import org.hjson.JsonValue;

public class IntConfig extends ConfigEntry<Integer> {

	public IntConfig(Integer defaultValue) {
		super(defaultValue);
	}

	@Override
	public void read(JsonValue json) {
		if(json.isNumber()){
			value = json.asInt();
		}
	}

	@Override
	public JsonValue write(Integer value) {
		return JsonValue.valueOf(value);
	}
}
