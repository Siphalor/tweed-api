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

package de.siphalor.tweed4.config.constraints;

import com.mojang.datafixers.util.Pair;

import java.util.Collections;
import java.util.List;

public interface Constraint<T> {
	Result<T> apply(T value);

	String getDescription();

	class Result<T> {
		public static final Result<?> OK = new Result<>(true, null, Collections.emptyList());

		public final boolean ok;
		public final List<Pair<Severity, String>> messages;
		public final T value;

		public Result(boolean ok, T value, List<Pair<Severity, String>> messages) {
			this.ok = ok;
			this.messages = messages;
			this.value = value;
		}
	}

	enum Severity { INFO, WARN, ERROR }
}
