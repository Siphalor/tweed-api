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

package de.siphalor.tweed4.tailor.coat;

import com.mojang.datafixers.util.Pair;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.CheckBoxConfigInput;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.list.entry.ConfigListConfigEntry;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.tweed4.config.ConfigCategory;
import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import de.siphalor.tweed4.tailor.coat.entryhandler.ConvertingConfigEntryHandler;
import de.siphalor.tweed4.tailor.coat.entryhandler.SimpleConfigEntryHandler;
import de.siphalor.tweed4.tailor.screen.ScreenTailor;
import de.siphalor.tweed4.tailor.screen.ScreenTailorScreenFactory;
import de.siphalor.tweed4.util.DirectListMultimap;
import de.siphalor.tweed4.util.StaticStringConvertible;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.*;
import java.util.function.Supplier;

public class CoatTailor extends ScreenTailor {
	private static final String TRANSLATION_PREFIX = "tweed4_tailor_screen.screen.";
	private static final DirectListMultimap<Class<?>, TweedCoatEntryProcessor<?>, LinkedList<TweedCoatEntryProcessor<?>>> CONVERTERS =
			new DirectListMultimap<>(new HashMap<>(), LinkedList::new);

	public static final CoatTailor INSTANCE = new CoatTailor();

	private final Map<String, ScreenTailorScreenFactory<?>> screenFactories = new HashMap<>();

	public static <T> void registerConverter(Class<T> clazz, TweedCoatEntryProcessor<T> entryConverter) {
		CONVERTERS.put(clazz, entryConverter);
	}

	public Map<String, ScreenTailorScreenFactory<?>> getScreenFactories() {
		return screenFactories;
	}

	@Override
	public void process(ConfigFile configFile) {
		String modId = configFile.getName();
		screenFactories.put(configFile.getName(), parent ->
			syncAndCreateScreen(configFile, parent_ -> {
				ConfigScreen configScreen = new ConfigScreen(
						parent_, new TranslatableText(TRANSLATION_PREFIX + modId),
						Collections.singletonList(convert(configFile.getRootCategory(), TRANSLATION_PREFIX + modId))
				);
				configScreen.setOnSave(() -> save(configFile));
				return configScreen;
			}, parent)
		);
	}

	public ConfigListWidget convert(ConfigCategory category, String path) {
		Text name = new TranslatableText(path);
		ConfigListWidget listWidget = new ConfigListWidget(
				MinecraftClient.getInstance(), name, Collections.emptyList(),
				category.getBackgroundTexture() != null
						? category.getBackgroundTexture()
						: DrawableHelper.OPTIONS_BACKGROUND_TEXTURE
		);

		category.entryStream().forEachOrdered(mapEntry -> {
			String subPath = path + "." + mapEntry.getKey();
			if (mapEntry.getValue() instanceof ValueConfigEntry) {
				process(listWidget, (ValueConfigEntry<?>) mapEntry.getValue(), subPath);
			} else if (mapEntry.getValue() instanceof ConfigCategory) {
				listWidget.addSubTree(convert((ConfigCategory) mapEntry.getValue(), subPath));
			}
		});
		return listWidget;
	}

	public <T> boolean process(ConfigListWidget parentWidget, ValueConfigEntry<T> configEntry, String path) {
		Class<?> type = configEntry.getType();

		while (type != null && type != Object.class) {
			//noinspection unchecked
			if (tryProcess(
					(LinkedList<TweedCoatEntryProcessor<T>>) (Object) CONVERTERS.get(type),
					parentWidget, configEntry, path
			)) {
				return true;
			}

			for (Class<?> anInterface : type.getInterfaces()) {
				//noinspection unchecked
				if (tryProcess(
						(LinkedList<TweedCoatEntryProcessor<T>>) (Object) CONVERTERS.get(anInterface),
						parentWidget, configEntry, path
				)) {
					return true;
				}
			}

			type = type.getSuperclass();
		}
		return false;
	}

	protected <T> boolean tryProcess(LinkedList<TweedCoatEntryProcessor<T>> entryProcessors, ConfigListWidget parentWidget, ValueConfigEntry<T> configEntry, String path) {
		if (entryProcessors == null) {
			return false;
		}
		Iterator<TweedCoatEntryProcessor<T>> iterator = entryProcessors.descendingIterator();
		while (iterator.hasNext()) {
			if (iterator.next().process(parentWidget, configEntry, path)) {
				return true;
			}
		}
		return false;
	}

	public static Message convert(Pair<Constraint.Severity, String> constraintMessage) {
		switch (constraintMessage.getFirst()) {
			default:
			case INFO:
				return new Message(Message.Level.INFO, new LiteralText(constraintMessage.getSecond()));
			case WARN:
				return new Message(Message.Level.WARNING, new LiteralText(constraintMessage.getSecond()));
			case ERROR:
				return new Message(Message.Level.ERROR, new LiteralText(constraintMessage.getSecond()));
		}
	}

	public static <V> ConfigListConfigEntry<V> convertSimpleConfigEntry(ValueConfigEntry<V> configEntry, String path, ConfigInput<V> configInput) {
		return new ConfigListConfigEntry<>(
				new TranslatableText(path),
				new TranslatableText(path + ".description"),
				new SimpleConfigEntryHandler<>(configEntry),
				configInput
		);
	}

	public static <V> ConfigListConfigEntry<V> convertSimpleConfigEntry(String path, ConfigInput<V> configInput, ConfigEntryHandler<V> entryHandler) {
		return new ConfigListConfigEntry<>(
				new TranslatableText(path),
				new TranslatableText(path + ".description"),
				entryHandler,
				configInput
		);
	}

	public static <V> Constraint.Result<V> wrapExceptions(Supplier<V> runnable) {
		try {
			return new Constraint.Result<>(true, runnable.get(), Collections.emptyList());
		} catch (Exception e) {
			return new Constraint.Result<>(
					false, null,
					Collections.singletonList(Pair.of(Constraint.Severity.ERROR, e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()))
			);
		}
	}

	static {
		registerConverter(String.class, (parentWidget, configEntry, path) -> {
			TextConfigInput textConfigInput = new TextConfigInput(configEntry.getValue());
			parentWidget.addEntry(convertSimpleConfigEntry(configEntry, path, textConfigInput));
			return true;
		});

		registerConverter(Boolean.class, (parentWidget, configEntry, path) -> {
			parentWidget.addEntry(convertSimpleConfigEntry(configEntry, path, new CheckBoxConfigInput(LiteralText.EMPTY, configEntry.getValue(), false)));
			return true;
		});

		registerConverter(StaticStringConvertible.class, (parentWidget, configEntry, path) -> {
			TextConfigInput textConfigInput = new TextConfigInput(configEntry.getValue().asString());
			parentWidget.addEntry(convertSimpleConfigEntry(path, textConfigInput, new ConvertingConfigEntryHandler<>(
					configEntry, StaticStringConvertible::asString, input -> wrapExceptions(() -> configEntry.getDefaultValue().valueOf(input))
			)));
			return true;
		});

		registerConverter(Byte.class, new TCNumberEntryProcessor<>(Byte::parseByte));
		registerConverter(Short.class, new TCNumberEntryProcessor<>(Short::parseShort));
		registerConverter(Integer.class, new TCNumberEntryProcessor<>(Integer::parseInt));
		registerConverter(Long.class, new TCNumberEntryProcessor<>(Long::parseLong));
		registerConverter(Float.class, new TCNumberEntryProcessor<>(Float::parseFloat));
		registerConverter(Double.class, new TCNumberEntryProcessor<>(Double::parseDouble));
	}
}
