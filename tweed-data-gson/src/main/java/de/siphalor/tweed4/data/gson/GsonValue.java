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

package de.siphalor.tweed4.data.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.ApiStatus;

public class GsonValue implements DataValue<GsonValue, GsonList, GsonObject> {
	protected final JsonElement jsonElement;

	GsonValue(JsonElement jsonElement) {
		this.jsonElement = jsonElement;
	}

	@Override
	public void setComment(String comment) {

	}

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public boolean isGenericNumber() {
		return isNumber();
	}

	@Override
	public boolean isNumber() {
		return JsonHelper.isNumber(jsonElement);
	}

	@Override
	public boolean isByte() {
		return isNumber();
	}

	@Override
	public boolean isShort() {
		return isNumber();
	}

	@Override
	public boolean isInt() {
		return isNumber();
	}

	@Override
	public boolean isLong() {
		return isNumber();
	}

	@Override
	public boolean isFloat() {
		return isNumber();
	}

	@Override
	public boolean isDouble() {
		return isNumber();
	}

	@Override
	public boolean isChar() {
		return isString() && asString().length() == 1;
	}

	@Override
	public boolean isString() {
		return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString();
	}

	@Override
	public boolean isBoolean() {
		return jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isBoolean();
	}

	@Override
	public boolean isObject() {
		return jsonElement.isJsonObject();
	}

	@Override
	public boolean isList() {
		return jsonElement.isJsonArray();
	}

	@Override
	public Number asNumber() {
		return jsonElement.getAsNumber();
	}

	@Override
	public byte asByte() {
		return jsonElement.getAsByte();
	}

	@Override
	public short asShort() {
		return jsonElement.getAsShort();
	}

	@Override
	public int asInt() {
		return jsonElement.getAsInt();
	}

	@Override
	public long asLong() {
		return jsonElement.getAsLong();
	}

	@Override
	public float asFloat() {
		return jsonElement.getAsFloat();
	}

	@Override
	public double asDouble() {
		return jsonElement.getAsDouble();
	}

	@Override
	public char asChar() {
		return jsonElement.getAsCharacter();
	}

	@Override
	public String asString() {
		return jsonElement.getAsString();
	}

	@Override
	public boolean asBoolean() {
		return jsonElement.getAsBoolean();
	}

	@Override
	public GsonObject asObject() {
		return new GsonObject(jsonElement);
	}

	@Override
	public GsonList asList() {
		return new GsonList(jsonElement);
	}

	@Override
	@ApiStatus.Internal
	public JsonElement getRaw() {
		return jsonElement;
	}
}
