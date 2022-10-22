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

package de.siphalor.tweed5.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnnotatedDataValue<V> {
	private final @NotNull V value;
	private final @Nullable String comment;

	protected AnnotatedDataValue(@NotNull V value, @Nullable String comment) {
		this.value = value;
		this.comment = comment;
	}

	public static <V> AnnotatedDataValue<V> of(V value) {
		return new AnnotatedDataValue<>(value, null);
	}

	public static <V> AnnotatedDataValue<V> of(V value, String comment) {
		return new AnnotatedDataValue<>(value, comment);
	}

	public @NotNull V getValue() {
		return value;
	}

	public @Nullable String getComment() {
		return comment;
	}
}
