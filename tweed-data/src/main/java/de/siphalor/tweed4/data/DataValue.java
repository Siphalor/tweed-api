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

import de.siphalor.tweed4.data.serializer.DataSerializer;

public interface DataValue<V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> {
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
	O asObject();
	L asList();

	/**
	 * Should only be used in {@link de.siphalor.tweed4.data.serializer.ConfigDataSerializer}
	 * @return the raw value
	 */
	@Deprecated
	Object getRaw();

	default <V2 extends DataValue<V2, L2, O2>, L2 extends DataList<V2, L2, O2>, O2 extends DataObject<V2, L2, O2>>
	boolean equals(DataValue<V2, L2, O2> other) {
		if (isNumber()) {
			if (isGenericNumber() && other.isGenericNumber()){
				Number num = asNumber();
				Number otherNum = other.asNumber();
				if (num.longValue() == otherNum.longValue() && num.doubleValue() == otherNum.doubleValue()) {
					return true;
				}
			}
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
			O object = asObject();
			O2 otherObject = other.asObject();
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
			L list = asList();
			L2 otherList = other.asList();
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

	@SuppressWarnings("unchecked")
	default <V2 extends DataValue<V2, L2, O2>, L2 extends DataList<V2, L2, O2>, O2 extends DataObject<V2, L2, O2>>
	V2 convert(DataSerializer<V2, L2, O2> otherSerializer) {
		if (isList()) {
			L2 otherList = otherSerializer.newList();
			L list = asList();
			for (int i = 0; i < list.size(); i++) {
				otherList.set(i, list.get(i).convert(otherSerializer));
			}
			return (V2) otherList;
		} else if (isObject()) {
			O2 otherObject = otherSerializer.newObject();
			O object = asObject();
			for (String key : object.keys()) {
				otherObject.set(key, object.get(key).convert(otherSerializer));
			}
			return (V2) otherObject;
		} else if (isBoolean()) {
			return otherSerializer.newBoolean(asBoolean());
		} else if (isString()) {
			return otherSerializer.newString(asString());
		} else if (isChar()) {
			return otherSerializer.newChar(asChar());
		} else if (isGenericNumber()) {
			Number number = asNumber();
			if (
					number.doubleValue() == Math.floor(number.doubleValue())
							&& number.doubleValue() == Math.ceil(number.doubleValue())
			) { // number is integral
				if (number.longValue() > Integer.MAX_VALUE) {
					return otherSerializer.newLong(number.longValue());
				} else if (number.intValue() > Short.MAX_VALUE) {
					return otherSerializer.newInt(number.intValue());
				} else if (number.shortValue() > Byte.MAX_VALUE) {
					return otherSerializer.newShort(number.shortValue());
				} else {
					return otherSerializer.newByte(number.byteValue());
				}
			} else {
				// too annoying to filter out floats tbh
				return otherSerializer.newDouble(number.doubleValue());
			}
		}

		if (isByte()) {
			return otherSerializer.newByte(asByte());
		} else if (isShort()) {
			return otherSerializer.newShort(asShort());
		} else if (isInt()) {
			return otherSerializer.newInt(asInt());
		} else if (isLong()) {
			return otherSerializer.newLong(asLong());
		} else if (isFloat()) {
			return otherSerializer.newFloat(asFloat());
		} else if (isDouble()) {
			return otherSerializer.newDouble(asDouble());
		}
		throw new RuntimeException("Failed to convert Tweed data \"" + this + "\" to " + otherSerializer.getId());
	}
}
