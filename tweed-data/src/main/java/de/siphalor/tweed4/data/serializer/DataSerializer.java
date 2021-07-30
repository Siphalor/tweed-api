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

package de.siphalor.tweed4.data.serializer;

import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;

@SuppressWarnings("deprecation")
public interface DataSerializer<V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> extends ConfigDataSerializer<V, L, O> {
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
}
