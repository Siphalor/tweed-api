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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class TweedIdentifier {
	protected static final Pattern PART_PATTERN = Pattern.compile("[a-z0-9_/]+");

	private final String namespace;
	private final String path;

	protected TweedIdentifier(String namespace, String path) {
		if (!PART_PATTERN.matcher(namespace).matches()) {
			throw new IllegalArgumentException("Invalid namespace: " + namespace);
		}
		if (!PART_PATTERN.matcher(path).matches()) {
			throw new IllegalArgumentException("Invalid path: " + path);
		}
		this.namespace = namespace;
		this.path = path;
	}

	public static TweedIdentifier of(String modId, String path) {
		return new TweedIdentifier(modId, path);
	}

	public static TweedIdentifier parse(String defaultNamespace, String string) {
		int colonIndex = string.indexOf(':');
		if (colonIndex == -1) {
			return of(defaultNamespace, string);
		}
		return of(string.substring(0, colonIndex), string.substring(colonIndex + 1));
	}

	public static TweedIdentifier parse(String string) {
		int colonIndex = string.indexOf(':');
		if (colonIndex == -1) {
			throw new IllegalArgumentException("Invalid identifier, missing namespace: " + string);
		}
		return of(string.substring(0, colonIndex), string.substring(colonIndex + 1));
	}

	public String getNamespace() {
		return namespace;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return namespace + ":" + path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TweedIdentifier that = (TweedIdentifier) o;
		return namespace.equals(that.namespace) && path.equals(that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, path);
	}
}
