package de.siphalor.tweed.config.entry;

import org.hjson.JsonValue;

public class StringConfig extends ConfigEntry<String> {
	public StringConfig(String defaultValue) {
		super(defaultValue);
	}

	@Override
	public void read(JsonValue json) {
		if(json.isString()) {
			value = json.asString();
		}
	}

	@Override
	public JsonValue write(String value) {
		return JsonValue.valueOf(value);
	}

}
