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

import java.util.function.Supplier;

public class DefinedXmlValue implements XmlValue {
	private final @NotNull String text;
	private final @Nullable String type;

	public DefinedXmlValue(@NotNull String text, @Nullable String type) {
		this.text = text;
		this.type = type;
	}


	@Override
	public @NotNull String getText() {
		return text;
	}

	@Override
	public @Nullable String getType() {
		return type;
	}

	@Override
	public @NotNull Element getElement(Supplier<Element> elementSupplier) {
		Element element = elementSupplier.get();
		element.setTextContent(text);
		if (type != null) {
			element.setAttribute("type", type);
		}
		return element;
	}

	@Override
	public boolean equals(Object obj) {
		return defaultEquals(obj);
	}
}
