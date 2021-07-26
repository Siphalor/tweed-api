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

	Number asNumber();
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

	default boolean equals(DataValue<?> other) {
		if (isNumber()) {
			return other.isNumber() && asNumber().equals(other.asNumber());
		} else if (isChar()) {
			return other.isChar() && asChar() == other.asChar();
		} else if (isString()) {
			return other.isString() && asString().equals(other.asString());
		} else if (isBoolean()) {
			return other.isBoolean() && asBoolean() == other.asBoolean();
		} else if (isObject()) {
			if (!other.isObject()) {
				return false;
			}
			DataObject<RawValue> object = asObject();
			DataObject<?> otherObject = other.asObject();
			if (!object.keys().equals(otherObject.keys())) {
				return false;
			}
			for (String key : object.keys()) {
				if (!object.get(key).equals(otherObject.get(key))) {
					return false;
				}
			}
			return true;
		} else if (isList()) {
			if (!other.isList()) {
				return false;
			}
			DataList<RawValue> list = asList();
			DataList<?> otherList = other.asList();
			if (list.size() != otherList.size()) {
				return false;
			}
			for (int i = 0; i < list.size(); i++) {
				if (!list.get(i).equals(otherList.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
