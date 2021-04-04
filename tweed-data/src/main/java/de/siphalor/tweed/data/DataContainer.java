package de.siphalor.tweed.data;

public interface DataContainer<RawValue, Key> extends DataValue<RawValue> {
	boolean has(Key key);
	int size();

	DataValue<RawValue> get(Key key);

	default boolean hasByte(Key key) {
		return has(key) && get(key).isByte();
	}
	default boolean hasShort(Key key) {
		return has(key) && get(key).isShort();
	}
	default boolean hasInt(Key key) {
		return has(key) && get(key).isInt();
	}
	default boolean hasLong(Key key) {
		return has(key) && get(key).isLong();
	}
	default boolean hasFloat(Key key) {
		return has(key) && get(key).isFloat();
	}
	default boolean hasDouble(Key key) {
		return has(key) && get(key).isDouble();
	}
	default boolean hasCharacter(Key key) {
		return has(key) && get(key).isChar();
	}
	default boolean hasString(Key key) {
		return has(key) && get(key).isString();
	}
	default boolean hasBoolean(Key key) {
		return has(key) && get(key).isBoolean();
	}
	default boolean hasObject(Key key) {
		return has(key) && get(key).isObject();
	}
	default boolean hasList(Key key) {
		return has(key) && get(key).isList();
	}

	default byte getByte(Key key, byte def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isByte()) {
			return value.asByte();
		}
		return def;
	}
	default short getShort(Key key, short def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isShort()) {
			return value.asShort();
		}
		return def;
	}
	default int getInt(Key key, int def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isInt()) {
			return value.asInt();
		}
		return def;
	}
	default long getLong(Key key, long def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isLong()) {
			return value.asLong();
		}
		return def;
	}
	default float getFloat(Key key, float def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isFloat()) {
			return value.asFloat();
		}
		return def;
	}
	default double getDouble(Key key, double def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isDouble()) {
			return value.asDouble();
		}
		return def;
	}
	default char getCharacter(Key key, char def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isChar()) {
			return value.asChar();
		}
		return def;
	}
	default String getString(Key key, String def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isString()) {
			return value.asString();
		}
		return def;
	}
	default boolean getBoolean(Key key, boolean def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isBoolean()) {
			return value.asBoolean();
		}
		return def;
	}
	default DataObject<RawValue> getObject(Key key, DataObject<RawValue> def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isObject()) {
			return value.asObject();
		}
		return def;
	}
	default DataList<RawValue> getByte(Key key, DataList<RawValue> def) {
		DataValue<RawValue> value = get(key);
		if (value != null && value.isList()) {
			return value.asList();
		}
		return def;
	}

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
