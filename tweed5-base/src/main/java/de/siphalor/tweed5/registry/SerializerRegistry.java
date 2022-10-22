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

package de.siphalor.tweed5.registry;

import de.siphalor.tweed5.data.DataSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SerializerRegistry extends Registry<TweedIdentifier, DataSerializer<?>> {
	private static final Map<String, DataSerializer<?>> BY_EXTENSION = new HashMap<>();

	public SerializerRegistry() {
		super();
	}

	@Override
	@Deprecated
	public DataSerializer<?> register(@NotNull TweedIdentifier key, @NotNull DataSerializer<?> value) {
		super.register(key, value);
		if (!key.equals(TweedIdentifier.parse(value.getId()))) {
			throw new IllegalArgumentException("The serializer's id must match the key");
		}

		String extension = value.getFileExtension();
		if (BY_EXTENSION.containsKey(extension)) {
			throw new IllegalArgumentException("Duplicate serializer file extension " + extension + " by " + key + ", already registered by " + BY_EXTENSION.get(extension).getId());
		}
		BY_EXTENSION.put(extension, value);
		return value;
	}

	public DataSerializer<?> register(DataSerializer<?> value) {
		return register(TweedIdentifier.parse(value.getId()), value);
	}

	public DataSerializer<?> getSerializerByExtension(String extension) {
		return BY_EXTENSION.get(extension);
	}

	public Map<String, DataSerializer<?>> getSerializersByExtension() {
		return BY_EXTENSION;
	}
}
