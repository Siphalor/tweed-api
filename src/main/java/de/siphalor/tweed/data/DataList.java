package de.siphalor.tweed.data;

public interface DataList<RawValue> extends DataValue<RawValue>, Iterable<DataValue<RawValue>> {
	int size();
	DataValue<RawValue> get(int index);

    DataValue<RawValue> set(int index, int value);
	DataValue<RawValue> set(int index, float value);
	DataValue<RawValue> set(int index, String value);
	DataValue<RawValue> set(int index, boolean value);
	DataValue<RawValue> set(int index, DataValue<RawValue> value);

	@Override
	default boolean isNumber() {
		return false;
	}

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
		return false;
	}

	@Override
	default boolean isList() {
		return true;
	}
}
