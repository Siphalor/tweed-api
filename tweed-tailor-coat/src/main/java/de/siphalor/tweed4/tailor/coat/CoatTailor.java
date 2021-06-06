package de.siphalor.tweed4.tailor.coat;

import com.mojang.datafixers.util.Pair;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
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
import de.siphalor.tweed4.tailor.Tailor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.*;

public class CoatTailor extends Tailor {
	private static final String TRANSLATION_PREFIX = "tweed4_tailor_coat.screen.";
	private static final Map<Class<?>, TweedCoatEntryProcessor<?>> CONVERTERS = new HashMap<>();

	public static final CoatTailor INSTANCE = new CoatTailor();

	private final Map<String, ConfigScreenFactory<?>> screenFactories = new HashMap<>();

	public static <T> void registerConverter(Class<T> clazz, TweedCoatEntryProcessor<T> entryConverter) {
		CONVERTERS.put(clazz, entryConverter);
	}

	public Map<String, ConfigScreenFactory<?>> getScreenFactories() {
		return screenFactories;
	}

	@Override
	public void process(ConfigFile configFile) {
		String modId = configFile.getName();
		screenFactories.put(configFile.getName(), parent -> {
			ConfigScreen configScreen = new ConfigScreen(
					parent, new TranslatableText(TRANSLATION_PREFIX + modId),
					Collections.singletonList(convert(configFile.getRootCategory(), TRANSLATION_PREFIX + modId))
			);
			configScreen.setOnSave(() -> {

			});
			return configScreen;
		});
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

	public void process(ConfigListWidget parentWidget, ValueConfigEntry<?> configEntry, String path) {
		Class<?> type = configEntry.getType();
		//noinspection rawtypes
		TweedCoatEntryProcessor entryConverter;

		while (type != null && type != Object.class) {
			entryConverter = CONVERTERS.get(type);
			if (entryConverter != null) {
				//noinspection unchecked
				entryConverter.process(parentWidget, configEntry, path);
			}

			for (Class<?> anInterface : type.getInterfaces()) {
				entryConverter = CONVERTERS.get(anInterface);
				if (entryConverter != null) {
					//noinspection unchecked
					entryConverter.process(parentWidget, configEntry, path);
				}
			}

			type = type.getSuperclass();
		}
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

	static {
		registerConverter(String.class, (parentWidget, configEntry, path) -> {
			TextConfigInput textConfigInput = new TextConfigInput(LiteralText.EMPTY);
			textConfigInput.setValue(configEntry.getValue());
			parentWidget.addEntry(convertSimpleConfigEntry(configEntry, path, textConfigInput));
		});

		registerConverter(Boolean.class, (parentWidget, configEntry, path) ->
				parentWidget.addEntry(convertSimpleConfigEntry(configEntry, path, new CheckBoxConfigInput(LiteralText.EMPTY, configEntry.getValue(), false)))
		);
	}
}
