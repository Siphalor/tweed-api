package de.siphalor.tweed.data;

public interface DataValue<RawValue> {
	void setComment(String comment);
	String getComment();

	boolean isNumber();
	boolean isString();
	boolean isBoolean();
	boolean isCompound();
	boolean isList();

	default boolean isEmpty() {
		if(isString()) return asString().isEmpty();
		if(isBoolean()) return asBoolean();
		if(isCompound()) return asCompound().isEmpty();
		if(isList()) return asList().isEmpty();
		return false;
	}

	int asInt();
	float asFloat();
	String asString();
	boolean asBoolean();
	DataObject<RawValue> asCompound();
	DataList<RawValue> asList();

	/**
	 * @deprecated Should only be used in {@link de.siphalor.tweed.data.serializer.ConfigDataSerializer}
	 * @return the raw value
	 */
	@Deprecated
	RawValue getRaw();
}
