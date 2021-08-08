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
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.DataValue;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A serializer that can read and write an abstract data representation to and from streams.
 * @param <V> The {@link DataValue} class associated with this serializer.
 * @param <L> The {@link DataList} class associated with this serializer. L MUST extend V.
 * @param <O> The {@link DataObject} class associated with this serializer. O MUST extend V.
 * @deprecated Extend {@link DataSerializer} instead.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface ConfigDataSerializer<V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>> {
	O newObject();
	O read(InputStream inputStream);
	void write(OutputStream outputStream, O dataObject);
	String getFileExtension();

	/**
	 * Should return the id of this serializer.
	 * Used when adding serializers through the <code>tweed4:serializer</code> entry point.
	 * @return The id of the serializer as <code>modid:name</code>
	 */
	default String getId() {
		return null;
	}
}
