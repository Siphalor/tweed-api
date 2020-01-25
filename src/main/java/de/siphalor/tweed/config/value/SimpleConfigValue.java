package de.siphalor.tweed.config.value;

public class SimpleConfigValue<V> extends ConfigValue<V> {
	V value;

	public SimpleConfigValue() {

	}

	public SimpleConfigValue(V value) {
		this.value = value;
	}

	@Override
	public V get() {
		return value;
	}

	@Override
	public void set(V value) {
		this.value = value;
	}
}
