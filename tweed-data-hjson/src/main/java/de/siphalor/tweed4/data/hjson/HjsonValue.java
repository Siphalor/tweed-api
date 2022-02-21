/*
 * Copyright 2021-2022 Siphalor
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

package de.siphalor.tweed4.data.hjson;

import de.siphalor.tweed4.data.DataValue;
import org.hjson.CommentStyle;
import org.hjson.CommentType;
import org.hjson.JsonValue;
import org.jetbrains.annotations.ApiStatus;

public class HjsonValue implements DataValue<HjsonValue, HjsonList, HjsonObject> {
	protected final JsonValue jsonValue;

	HjsonValue(JsonValue jsonValue) {
		this.jsonValue = jsonValue;
	}

	@Override
	public void setComment(String comment) {
		jsonValue.setComment(CommentType.BOL, CommentStyle.LINE, comment);
	}

	@Override
	public String getComment() {
		return jsonValue.getBOLComment();
	}

	@Override
	public boolean isGenericNumber() {
		return isNumber();
	}

	@Override
	public boolean isNumber() {
		return jsonValue.isNumber();
	}

	@Override
	public boolean isByte() {
		return jsonValue.isNumber();
	}

	@Override
	public boolean isShort() {
		return jsonValue.isNumber();
	}

	@Override
	public boolean isInt() {
		return jsonValue.isNumber();
	}

	@Override
	public boolean isLong() {
		return jsonValue.isNumber();
	}

	@Override
	public boolean isFloat() {
		return jsonValue.isNumber();
	}

	@Override
	public boolean isDouble() {
		return jsonValue.isNumber();
	}

	@Override
	public boolean isChar() {
		return jsonValue.isString() && jsonValue.asString().length() == 1;
	}

	@Override
	public boolean isString() {
		return jsonValue.isString();
	}

	@Override
	public boolean isBoolean() {
		return jsonValue.isBoolean();
	}

	@Override
	public boolean isObject() {
		return jsonValue.isObject();
	}

	@Override
	public boolean isList() {
		return jsonValue.isArray();
	}

	@Override
	public boolean isNull() {
		return jsonValue.isNull();
	}

	@Override
	public Number asNumber() {
		return jsonValue.asDouble();
	}

	@Override
	public byte asByte() {
		return (byte) jsonValue.asInt();
	}

	@Override
	public short asShort() {
		return (short) jsonValue.asInt();
	}

	@Override
	public int asInt() {
		return jsonValue.asInt();
	}

	@Override
	public long asLong() {
		return jsonValue.asLong();
	}

	@Override
	public float asFloat() {
		return jsonValue.asFloat();
	}

	@Override
	public double asDouble() {
		return jsonValue.asDouble();
	}

	@Override
	public char asChar() {
		return jsonValue.asString().charAt(0);
	}

	@Override
	public String asString() {
		return jsonValue.asString();
	}

	@Override
	public boolean asBoolean() {
		return jsonValue.asBoolean();
	}

	@Override
	public HjsonObject asObject() {
		return new HjsonObject(jsonValue.asObject());
	}

	@Override
	public HjsonList asList() {
		return new HjsonList(jsonValue.asArray());
	}

	@Override
	@ApiStatus.Internal
	public JsonValue getRaw() {
		return jsonValue;
	}
}
