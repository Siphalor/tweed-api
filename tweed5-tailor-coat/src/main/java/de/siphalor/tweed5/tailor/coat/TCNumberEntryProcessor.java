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

package de.siphalor.tweed5.tailor.coat;

import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.tweed5.config.entry.ValueConfigEntry;
import de.siphalor.tweed5.tailor.coat.entryhandler.ConvertingConfigEntryHandler;

import java.util.function.Function;

public class TCNumberEntryProcessor<N extends Number> implements TweedCoatEntryProcessor<N> {
	private final Function<String, N> parseFunction;

	public TCNumberEntryProcessor(Function<String, N> parseFunction) {
		this.parseFunction = parseFunction;
	}

	@Override
	public boolean process(ConfigListWidget parentWidget, ValueConfigEntry<N> configEntry, String path) {
		TextConfigInput textConfigInput = new TextConfigInput(configEntry.getMainConfigValue().toString());
		parentWidget.addEntry(CoatTailor.convertSimpleConfigEntry(
				configEntry, path, textConfigInput,
				new ConvertingConfigEntryHandler<>(
						configEntry, Object::toString, input -> CoatTailor.wrapExceptions(() -> parseFunction.apply(input))
				)));
		return true;
	}
}
