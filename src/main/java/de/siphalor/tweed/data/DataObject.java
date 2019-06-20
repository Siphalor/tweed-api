package de.siphalor.tweed.data;

import com.mojang.datafixers.util.Pair;

public interface DataObject<RawValue> extends Iterable<Pair<String, DataValue<RawValue>>>, DataValue<RawValue> {
	boolean has(String key);
	DataValue<RawValue> get(String key);

	DataValue<RawValue> set(String key, int value);
	DataValue<RawValue> set(String key, float value);
	DataValue<RawValue> set(String key, String value);
	DataValue<RawValue> set(String key, boolean value);
	DataValue<RawValue> set(String key, DataValue<RawValue> value);

	DataObject<RawValue> addCompound(String key);
	DataList<RawValue> addList(String key);

	void remove(String key);

	@Override
	default boolean isNumber() {
		return false;
	};

	@Override
	default boolean isString() {
		return false;
	}

	@Override
	default boolean isBoolean() {
		return false;
	}

	@Override
	default boolean isCompound() {
		return true;
	}

	@Override
	default boolean isList() {
		return false;
	}

	@Override
	default int asInt() {
		return 0;
	}

	@Override
	default float asFloat() {
		return 0;
	}

	@Override
	default boolean asBoolean() {
		return !isEmpty();
	}

	@Override
	default DataObject<RawValue> asCompound() {
		return this;
	}

	@Override
	default DataList<RawValue> asList() {
		return null;
	}
}
