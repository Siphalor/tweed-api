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

package de.siphalor.tweed4.tailor.coat.entryhandler;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import de.siphalor.tweed4.tailor.coat.CoatTailor;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class SimpleConfigEntryHandler<V> implements ConfigEntryHandler<V> {
	private final ValueConfigEntry<V> configEntry;

	public SimpleConfigEntryHandler(ValueConfigEntry<V> configEntry) {
		this.configEntry = configEntry;
	}

	@Override
	public V getDefault() {
		return configEntry.getDefaultValue();
	}

	@Override
	public @NotNull Collection<Message> getMessages(V value) {
		Constraint.Result<V> result = configEntry.applyConstraints(value);
		return result.messages.stream().map(CoatTailor::convert).collect(Collectors.toList());
	}

	@Override
	public void save(V value) {
		configEntry.setMainConfigValue(value);
	}

	@Override
	public Text asText(V value) {
		return new LiteralText(configEntry.getValueSerializer().asString(value));
	}
}
