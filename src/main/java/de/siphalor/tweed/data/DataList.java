package de.siphalor.tweed.data;

public interface DataList<RawValue> extends Iterable<DataValue<RawValue>>, DataContainer<RawValue, Integer> {
	@Override
	default boolean has(Integer index) {
		return index < size();
	}

	@Override
	DataValue<RawValue> get(Integer index);

	@Override
	DataValue<RawValue> set(Integer index, int value);

	@Override
	DataValue<RawValue> set(Integer index, float value);

	@Override
	DataValue<RawValue> set(Integer index, String value);

	@Override
	DataValue<RawValue> set(Integer index, boolean value);

	@Override
	DataValue<RawValue> set(Integer index, DataValue<RawValue> value);

	@Override
	DataList<RawValue> addList(Integer index);

	@Override
	DataObject<RawValue> addObject(Integer index);

	@Override
	void remove(Integer index);

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
	default boolean isObject() {
		return false;
	}

	@Override
	default boolean isList() {
		return true;
	}
}
