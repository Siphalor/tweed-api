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

package de.siphalor.tweed4.data.xml.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.Objects;
import java.util.function.Supplier;

public interface XmlValue {
	@NotNull String getText();
	@Nullable String getType();

	@NotNull Element getElement(Supplier<Element> elementSupplier);

	default boolean defaultEquals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof XmlValue) {
			return getText().equals(((XmlValue) other).getText()) && Objects.equals(getType(), ((XmlValue) other).getType());
		}
		return false;
	}
}
