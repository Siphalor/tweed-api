/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed4.data;

public interface DataValue<RawValue> {
	void setComment(String comment);
	String getComment();

	default boolean isGenericNumber() {
		return false;
	}
	boolean isNumber();
	boolean isByte();
	boolean isShort();
	boolean isInt();
	boolean isLong();
	boolean isFloat();
	boolean isDouble();
	boolean isChar();
	boolean isString();
	boolean isBoolean();
	boolean isObject();
	boolean isList();

	default boolean isEmpty() {
		if(isString()) return asString().isEmpty();
		if(isBoolean()) return asBoolean();
		if(isObject()) return asObject().isEmpty();
		if(isList()) return asList().isEmpty();
		return false;
	}

	default Number asNumber() {
		return null;
	}
	byte asByte();
	short asShort();
	int asInt();
	long asLong();
	float asFloat();
	double asDouble();
	char asChar();
	String asString();
	boolean asBoolean();
	DataObject<RawValue> asObject();
	DataList<RawValue> asList();

	/**
	 * Should only be used in {@link de.siphalor.tweed4.data.serializer.ConfigDataSerializer}
	 * @return the raw value
	 */
	RawValue getRaw();
}
