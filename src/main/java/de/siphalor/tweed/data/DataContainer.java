package de.siphalor.tweed.data;

public interface DataContainer<RawValue, Key> extends DataValue<RawValue> {
	boolean has(Key key);
	int size();

	DataValue<RawValue> get(Key key);

	DataValue<RawValue> set(Key key, byte value);
	DataValue<RawValue> set(Key key, short value);
	DataValue<RawValue> set(Key key, int value);
	DataValue<RawValue> set(Key key, long value);
	DataValue<RawValue> set(Key key, float value);
	DataValue<RawValue> set(Key key, double value);
	DataValue<RawValue> set(Key key, char value);
	DataValue<RawValue> set(Key key, String value);
	DataValue<RawValue> set(Key key, boolean value);
	DataValue<RawValue> set(Key key, DataValue<RawValue> value);

	DataObject<RawValue> addObject(Key key);
	DataList<RawValue> addList(Key key);

	void remove(Key key);
}
