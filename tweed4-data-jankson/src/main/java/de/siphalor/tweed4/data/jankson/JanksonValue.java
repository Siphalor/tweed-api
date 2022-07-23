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

package de.siphalor.tweed4.data.jankson;

import blue.endless.jankson.*;
import de.siphalor.tweed4.data.DataValue;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JanksonValue implements DataValue<JanksonValue, JanksonList, JanksonObject> {
	protected final JsonElement element;
	Consumer<String> setComment;
	Supplier<String> getComment;
	Function<Class<?>, Object> as;

	JanksonValue(JsonElement jsonElement) {
		this(jsonElement, JanksonSerializer.SET_COMMENT_VOID, JanksonSerializer.GET_COMMENT_VOID, JanksonSerializer.AS_VOID);
	}

	JanksonValue(JsonElement jsonElement, Consumer<String> setComment, Supplier<String> getComment, Function<Class<?>, Object> as) {
		this.element = jsonElement;
		this.setComment = setComment;
		this.getComment = getComment;
		this.as = as;
	}

	@Override
	public void setComment(String comment) {
		setComment.accept(comment);
	}

	@Override
	public String getComment() {
		return getComment.get();
	}

	@Override
	public boolean isGenericNumber() {
		return isNumber();
	}

	@Override
	public boolean isNumber() {
		return element instanceof JsonPrimitive && ((JsonPrimitive) element).getValue() instanceof Number;
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
		return element instanceof JsonPrimitive && (
				(((JsonPrimitive) element).getValue() instanceof String && ((String) ((JsonPrimitive) element).getValue()).length() == 1)
						|| ((JsonPrimitive) element).getValue() instanceof Character
		);
	}

	@Override
	public boolean isString() {
		return element instanceof JsonPrimitive && ((JsonPrimitive) element).getValue() instanceof String;
	}

	@Override
	public boolean isBoolean() {
		return element instanceof JsonPrimitive && ((JsonPrimitive) element).getValue() instanceof Boolean;
	}

	@Override
	public boolean isObject() {
		return element instanceof JsonObject;
	}

	@Override
	public boolean isList() {
		return element instanceof JsonArray;
	}

	@Override
	public boolean isNull() {
		return element instanceof JsonNull;
	}

	@Override
	public Number asNumber() {
		return (Number) as.apply(Double.class);
	}

	@Override
	public byte asByte() {
		return (byte) as.apply(Byte.TYPE);
	}

	@Override
	public short asShort() {
		return (short) as.apply(Short.TYPE);
	}

	@Override
	public int asInt() {
		return (int) as.apply(Integer.TYPE);
	}

	@Override
	public long asLong() {
		return (long) as.apply(Long.TYPE);
	}

	@Override
	public float asFloat() {
		return (float) as.apply(Float.TYPE);
	}

	@Override
	public double asDouble() {
		return (double) as.apply(Double.TYPE);
	}

	@Override
	public char asChar() {
		return (char) as.apply(Character.TYPE);
	}

	@Override
	public String asString() {
		return (String) as.apply(String.class);
	}

	@Override
	public boolean asBoolean() {
		return (Boolean) as.apply(Boolean.TYPE);
	}

	@Override
	public JanksonObject asObject() {
		return new JanksonObject(element, setComment, getComment, as);
	}

	@Override
	public JanksonList asList() {
		return new JanksonList(element, setComment, getComment, as);
	}

	@Override
	@ApiStatus.Internal
	public JsonElement getRaw() {
		return element;
	}
}
