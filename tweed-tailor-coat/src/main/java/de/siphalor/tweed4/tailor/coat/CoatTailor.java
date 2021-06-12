package de.siphalor.tweed4.tailor.coat;

import com.mojang.datafixers.util.Pair;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
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
import de.siphalor.tweed4.tailor.Tailor;
import de.siphalor.tweed4.tailor.coat.entryhandler.ConvertingConfigEntryHandler;
import de.siphalor.tweed4.tailor.coat.entryhandler.SimpleConfigEntryHandler;
import de.siphalor.tweed4.util.DirectListMultimap;
import de.siphalor.tweed4.util.StaticStringConvertible;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.*;
import java.util.function.Supplier;

public class CoatTailor extends Tailor {
	private static final String TRANSLATION_PREFIX = "tweed4_tailor_coat.screen.";
	private static final DirectListMultimap<Class<?>, TweedCoatEntryProcessor<?>, LinkedList<TweedCoatEntryProcessor<?>>> CONVERTERS =
			new DirectListMultimap<>(new HashMap<>(), LinkedList::new);

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

	public static <V, W> ConfigListConfigEntry<W> convertSimpleConfigEntry(String path, ConfigInput<W> configInput, ConfigEntryHandler<W> entryHandler) {
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
