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

package de.siphalor.tweed4.data;

import de.siphalor.tweed4.data.serializer.ConfigDataSerializer;

import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("deprecation")
public interface DataSerializer<V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> extends ConfigDataSerializer<V, L, O> {
	/**
	 * @deprecated Use {@link DataSerializer#readValue(InputStream)} instead and check for the data type accordingly.
	 */
	@Override
	@Deprecated
	default O read(InputStream inputStream) {
		V v = readValue(inputStream);
		if (v.isObject()) {
			return v.asObject();
		} else {
			return null;
		}
	}

	V readValue(InputStream inputStream);

	/**
	 * @deprecated Use {@link DataSerializer#writeValue(OutputStream, DataValue)} instead.
	 */
	@Override
	@Deprecated
	default void write(OutputStream outputStream, O dataObject) {
		//noinspection unchecked
		writeValue(outputStream, (V) dataObject);
	}


	void writeValue(OutputStream outputStream, V dataValue);

	L newList();
	V newBoolean(boolean value);
	V newChar(char value);
	V newString(String value);
	V newByte(byte value);
	V newShort(short value);
	V newInt(int value);
	V newLong(long value);
	V newFloat(float value);
	V newDouble(double value);
	/**
	 * This should create a new null representative {@link DataValue}.
	 * <i>This should always be overridden. The default is here for legacy reasons.</i>
	 * @return A null representation.
	 * This should not be <code>null</code> itself since that indicates that null is not supported.
	 * @since 1.2
	 */
	default V newNull() {
		return null;
	}
}
