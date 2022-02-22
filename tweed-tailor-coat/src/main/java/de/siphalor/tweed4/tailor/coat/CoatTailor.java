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

package de.siphalor.tweed4.tailor.coat;

import com.mojang.datafixers.util.Pair;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.CheckBoxConfigInput;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.SliderConfigInput;
import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.list.entry.ConfigListConfigEntry;
import de.siphalor.coat.list.entry.ConfigListTextEntry;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.tweed4.config.ConfigCategory;
import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.config.constraints.Constraint;
import de.siphalor.tweed4.config.constraints.RangeConstraint;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import de.siphalor.tweed4.tailor.DropdownMaterial;
import de.siphalor.tweed4.tailor.coat.entry.CoatDropdownSelectInput;
import de.siphalor.tweed4.tailor.coat.entryhandler.ConvertingConfigEntryHandler;
import de.siphalor.tweed4.tailor.coat.entryhandler.SimpleConfigEntryHandler;
import de.siphalor.tweed4.tailor.screen.ScreenTailor;
import de.siphalor.tweed4.tailor.screen.ScreenTailorScreenFactory;
import de.siphalor.tweed4.util.DirectListMultimap;
import de.siphalor.tweed4.util.StaticStringConvertible;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Supplier;

public class CoatTailor extends ScreenTailor {
	private static final String TRANSLATION_PREFIX = "tweed4_tailor_screen.screen.";
	private static final String ENUM_TRANSLATION_PREFIX = "tweed4_tailor_screen.enum.";
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

		if (!category.getDescription().isEmpty()) {
			listWidget.addEntry(new ConfigListTextEntry(
					getTranslation(path + ".description", category.getDescription()).formatted(Formatting.GRAY)
			));
		}

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

	public static BaseText getTranslation(String key, String fallback) {
		if (I18n.hasTranslation(key)) {
			return new TranslatableText(key);
		}
		return new LiteralText(fallback == null ? key : fallback.replace("\t", "    "));
	}

	public static <V> ConfigListConfigEntry<V> convertSimpleConfigEntry(ValueConfigEntry<V> configEntry, String path, ConfigInput<V> configInput) {
		return new ConfigListConfigEntry<>(
				new TranslatableText(path),
				getTranslation(path + ".description", configEntry.getComment()),
				new SimpleConfigEntryHandler<>(configEntry),
				configInput
		);
	}

	public static <V> ConfigListConfigEntry<V> convertSimpleConfigEntry(ValueConfigEntry<?> configEntry, String path, ConfigInput<V> configInput, ConfigEntryHandler<V> entryHandler) {
		return new ConfigListConfigEntry<>(
				new TranslatableText(path),
				getTranslation(path + ".description", configEntry.getComment()),
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
			TextConfigInput textConfigInput = new TextConfigInput(configEntry.getMainConfigValue());
			parentWidget.addEntry(convertSimpleConfigEntry(configEntry, path, textConfigInput));
			return true;
		});

		registerConverter(Boolean.class, (parentWidget, configEntry, path) -> {
			parentWidget.addEntry(convertSimpleConfigEntry(configEntry, path, new CheckBoxConfigInput(LiteralText.EMPTY, configEntry.getMainConfigValue(), false)));
			return true;
		});

		registerConverter(StaticStringConvertible.class, (parentWidget, configEntry, path) -> {
			TextConfigInput textConfigInput = new TextConfigInput(configEntry.getMainConfigValue().asString());
			parentWidget.addEntry(convertSimpleConfigEntry(configEntry, path, textConfigInput, new ConvertingConfigEntryHandler<>(
					configEntry, StaticStringConvertible::asString, input -> wrapExceptions(() -> configEntry.getDefaultValue().valueOf(input))
			)));
			return true;
		});


		registerConverter(Enum.class, (parentWidget, configEntry, path) -> {
			//noinspection rawtypes
			Class<Enum> type = configEntry.getType();
			String enumTranslationKey = ENUM_TRANSLATION_PREFIX + type.getPackage().getName() + "." + type.getSimpleName() + ".";
			//noinspection rawtypes
			CoatDropdownSelectInput<Enum> input = new CoatDropdownSelectInput<>(
					configEntry.getMainConfigValue(),
					type.getEnumConstants(),
					val -> {
						String key = enumTranslationKey + val.name();
						if (I18n.hasTranslation(key)) {
							return new TranslatableText(key);
						} else {
							return new LiteralText(val.name());
						}
					}
			);
			//noinspection rawtypes
			ConfigListConfigEntry<Enum> entry = convertSimpleConfigEntry(configEntry, path, input);
			input.setParent(entry);
			parentWidget.addEntry(entry);
			return true;
		});
		registerConverter(DropdownMaterial.class,(parentWidget, configEntry, path) -> {
			//noinspection rawtypes,unchecked,SimplifyStreamApiCallChains
			CoatDropdownSelectInput<DropdownMaterial> input = new CoatDropdownSelectInput<>(
					configEntry.getMainConfigValue(),
					(DropdownMaterial[]) configEntry.getDefaultValue().values().stream().toArray(DropdownMaterial[]::new),
					val -> new TranslatableText(val.getTranslationKey()));
			//noinspection rawtypes
			ConfigListConfigEntry<DropdownMaterial> entry = convertSimpleConfigEntry(configEntry, path, input);
			input.setParent(entry);
			parentWidget.addEntry(entry);
			return true;
		});

		registerConverter(Byte.class, new TCNumberEntryProcessor<>(Byte::parseByte));
		registerConverter(Short.class, new TCNumberEntryProcessor<>(Short::parseShort));
		registerConverter(Integer.class, new TCNumberEntryProcessor<>(Integer::parseInt));
		registerConverter(Long.class, new TCNumberEntryProcessor<>(Long::parseLong));
		registerConverter(Float.class, new TCNumberEntryProcessor<>(Float::parseFloat));
		registerConverter(Double.class, new TCNumberEntryProcessor<>(Double::parseDouble));

		TweedCoatEntryProcessor<Number> numberEntryProcessor = (parentWidget, configEntry, path) -> {
			for (Constraint<Number> constraint : configEntry.getConstraints()) {
				if (constraint instanceof RangeConstraint) {
					parentWidget.addEntry(convertSimpleConfigEntry(
							configEntry,
							path,
							new SliderConfigInput<>(
									configEntry.getMainConfigValue(),
									((RangeConstraint<Number>) constraint).getMin(),
									((RangeConstraint<Number>) constraint).getMax()
							),
							new SimpleConfigEntryHandler<>(configEntry)
					));
					return true;
				}
			}
			return false;
		};
		//noinspection unchecked,rawtypes
		registerConverter(Byte.class, ((TweedCoatEntryProcessor) numberEntryProcessor));
		//noinspection unchecked,rawtypes
		registerConverter(Short.class, ((TweedCoatEntryProcessor) numberEntryProcessor));
		//noinspection unchecked,rawtypes
		registerConverter(Integer.class, ((TweedCoatEntryProcessor) numberEntryProcessor));
		//noinspection unchecked,rawtypes
		registerConverter(Long.class, ((TweedCoatEntryProcessor) numberEntryProcessor));
		//noinspection unchecked,rawtypes
		registerConverter(Float.class, ((TweedCoatEntryProcessor) numberEntryProcessor));
		//noinspection unchecked,rawtypes
		registerConverter(Double.class, ((TweedCoatEntryProcessor) numberEntryProcessor));
	}
}
