package de.siphalor.tweed.tailor;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.client.CustomNoticeScreen;
import de.siphalor.tweed.client.TweedClient;
import de.siphalor.tweed.client.cloth.ClothDropdownSelectEntry;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.config.entry.ValueConfigEntry;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.netty.buffer.Unpooled;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ClothTailor extends Tailor {
	public static final ClothTailor INSTANCE = new ClothTailor();

	protected static final String SCREEN_NAME_PREFIX = "tweed.cloth.";
	protected boolean waitingForFile;

	private static final Map<Class<?>, EntryConverter<?>> ENTRY_CONVERTERS = new HashMap<>();
	private final Map<String, ConfigScreenFactory<?>> screenFactories = new HashMap<>();

	@Override
	public void process(ConfigFile configFile) {
		String modId = configFile.getName();
		ClothData clothData = configFile.getTailorAnnotation(ClothData.class);
		if (clothData != null && !clothData.modid().isEmpty()) {
			modId = clothData.modid();
		}
		screenFactories.put(modId, parent -> convert(configFile, parent));
	}

	public Map<String, ConfigScreenFactory<?>> getScreenFactories() {
		return screenFactories;
	}

	public Screen convert(ConfigFile configFile, Screen parentScreen) {
		boolean inGame = MinecraftClient.getInstance().world != null;
		if (inGame && configFile.getRootCategory().getEnvironment() != ConfigEnvironment.CLIENT) {
			return new CustomNoticeScreen(
					() -> {
						waitingForFile = true;

						PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
						buf.writeString(configFile.getName());
						buf.writeEnumConstant(ConfigEnvironment.UNIVERSAL);
						buf.writeEnumConstant(ConfigScope.SMALLEST);
						buf.writeEnumConstant(ConfigOrigin.MAIN);
						ClientSidePacketRegistry.INSTANCE.sendToServer(Tweed.REQUEST_SYNC_C2S_PACKET, buf);

						TweedClient.setSyncRunnable(() -> {
							if (waitingForFile) {
								waitingForFile = false;
								MinecraftClient.getInstance().openScreen(buildConfigScreen(configFile, parentScreen));
							}
						});
					},
					() -> {
						waitingForFile = false;
						MinecraftClient.getInstance().openScreen(parentScreen);
					},
					new TranslatableText("tweed.gui.screen.syncFromServer"),
					new TranslatableText("tweed.gui.screen.syncFromServer.note")
			);
		} else {
			return buildConfigScreen(configFile, parentScreen);
		}
	}

	public Screen buildConfigScreen(ConfigFile configFile, Screen parentScreen) {
		final String path = SCREEN_NAME_PREFIX + configFile.getName();
		ConfigBuilder configBuilder = ConfigBuilder.create();
		configBuilder.setParentScreen(parentScreen);
		if (configFile.getRootCategory().getBackgroundTexture() != null) {
			configBuilder.setDefaultBackgroundTexture(configFile.getRootCategory().getBackgroundTexture());
		}
		configBuilder.setSavingRunnable(() -> save(configFile));
		configBuilder.setTitle(new TranslatableText(path));

		if (configFile.getRootCategory().entryStream().allMatch(entry -> entry.getValue() instanceof ConfigCategory)) {
			configFile.getRootCategory().entryStream().forEach(entry -> createCategory(configBuilder, (ConfigCategory) entry.getValue(), path + "." + entry.getKey()));
		} else {
			createCategory(configBuilder, configFile.getRootCategory(), path + ".main");
		}

		return configBuilder.build();
	}

	private void convertCategory(ConfigEntryBuilder entryBuilder, Consumer<AbstractConfigListEntry<?>> registry, ConfigCategory configCategory, String path) {
		configCategory.entryStream().forEachOrdered(entry -> {
			final String subPath = path + "." + entry.getKey();

			if (entry.getValue() instanceof ConfigCategory) {
				SubCategoryBuilder categoryBuilder = entryBuilder.startSubCategory(new TranslatableText(subPath));
				categoryBuilder.add(entryBuilder.startTextDescription(translation(subPath + ".description", entry.getValue().getDescription()).formatted(Formatting.GRAY)).build());

				convertCategory(entryBuilder, categoryBuilder::add, (ConfigCategory) entry.getValue(), subPath);

				registry.accept(categoryBuilder.build());

			} else if (entry.getValue() instanceof ValueConfigEntry<?>) {
				ValueConfigEntry<?> valueEntry = ((ValueConfigEntry<?>) entry.getValue());
				EntryConverter<?> entryConverter = valueEntry.getCustomEntryConverter();
				if (entryConverter == null) {
					Class<?> clazz = ((ValueConfigEntry<?>) entry.getValue()).getType();
					entryConverter = ENTRY_CONVERTERS.get(clazz);
					main:
					while (clazz != Object.class && entryConverter == null) {
						for (Class<?> anInterface : clazz.getInterfaces()) {
							entryConverter = ENTRY_CONVERTERS.get(anInterface);
							if (entryConverter != null) {
								break main;
							}
						}

						clazz = clazz.getSuperclass();
						entryConverter = ENTRY_CONVERTERS.get(clazz);
					}
				}

				if (entryConverter != null) {
					//noinspection unchecked,rawtypes
					registry.accept(entryConverter.convert((ValueConfigEntry) entry.getValue(), entryBuilder, subPath));
				} else {
					Tweed.LOGGER.warn("Couldn't convert config entry of type " + ((ValueConfigEntry<?>) entry.getValue()).getType().getSimpleName() + " to cloth entry!");
				}
			}
		});
	}

	private void createCategory(ConfigBuilder configBuilder, ConfigCategory configCategory, String name) {
		me.shedaniel.clothconfig2.api.ConfigCategory clothCategory = configBuilder.getOrCreateCategory(new TranslatableText(name));
		if (configCategory.getBackgroundTexture() != null) {
			clothCategory.setCategoryBackground(configCategory.getBackgroundTexture());
		}
		clothCategory.addEntry(configBuilder.entryBuilder().startTextDescription(translation(name + ".description", configCategory.getDescription()).formatted(Formatting.GRAY)).build());
		convertCategory(configBuilder.entryBuilder(), clothCategory::addEntry, configCategory, name);
	}

	private void save(ConfigFile configFile) {
		if (TweedClient.isOnRemoteServer()) {
			configFile.syncToServer(ConfigEnvironment.UNIVERSAL, ConfigScope.SMALLEST);
			ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
			ConfigLoader.loadConfigs(MinecraftClient.getInstance().getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.SMALLEST);
		} else {
			ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
			ConfigLoader.loadConfigs(MinecraftClient.getInstance().getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD);
		}
	}

	public static <V> void registerEntryConverter(Class<V> valueType, EntryConverter<V> converter) {
		ENTRY_CONVERTERS.put(valueType, converter);
	}

	public static <V> Optional<Text> errorSupplier(V value, ValueConfigEntry<V> configEntry) {
		try {
			configEntry.applyConstraints(value);
		} catch (ConstraintException e) {
			return Optional.of(new LiteralText(e.getMessage()));
		}
		return Optional.empty();
	}

	public static boolean requiresRestart(ValueConfigEntry<?> configEntry) {
		if (MinecraftClient.getInstance().world == null) {
			return configEntry.getScope().triggers(ConfigScope.GAME);
		} else {
			return configEntry.getScope().triggers(ConfigScope.WORLD);
		}
	}

	public static BaseText translation(String langKey, String defaultText) {
		if (I18n.hasTranslation(langKey)) {
			return new TranslatableText(langKey);
		}
		if (defaultText == null || defaultText.isEmpty()) {
			return null;
		}
		return new LiteralText(defaultText);
	}

	public static Optional<Text[]> description(String langKey, ValueConfigEntry<?> configEntry) {
		if (I18n.hasTranslation(langKey + ".description")) {
			return Optional.of(new Text[]{new TranslatableText(langKey + ".description")});
		}
		return configEntry.getClothyDescription();
	}

	@FunctionalInterface
	public interface EntryConverter<V> {
		AbstractConfigListEntry<?> convert(ValueConfigEntry<V> configEntry, ConfigEntryBuilder entryBuilder, String langKey);
	}

	static {
		registerEntryConverter(Boolean.class, (configEntry, entryBuilder, langKey) -> {
					BooleanToggleBuilder builder = entryBuilder.startBooleanToggle(new TranslatableText(langKey), configEntry.getMainConfigValue());
					builder.setDefaultValue(configEntry::getDefaultValue);
					builder.setSaveConsumer(configEntry::setMainConfigValue);
					builder.setTooltip(description(langKey, configEntry));
					builder.setErrorSupplier(value -> errorSupplier(value, configEntry));
					if (requiresRestart(configEntry)) {
						builder.requireRestart(true);
					}
					return builder.build();
				}
		);
		//noinspection unchecked,rawtypes
		registerEntryConverter(DropdownMaterial.class, (configEntry, entryBuilder, langKey) ->
				new ClothDropdownSelectEntry<>(
						new TranslatableText(langKey),
						configEntry.getMainConfigValue(),
						new TranslatableText("text.cloth-config.reset_value"),
						() -> description(langKey, configEntry),
						requiresRestart(configEntry),
						configEntry::getDefaultValue,
						configEntry::setMainConfigValue,
						new ArrayList<DropdownMaterial>(configEntry.getDefaultValue().values()),
						dropdownMaterial -> new TranslatableText(dropdownMaterial.getTranslationKey())
				)
		);
		registerEntryConverter(Enum.class, (configEntry, entryBuilder, langKey) -> {
					//noinspection unchecked
					EnumSelectorBuilder<Enum<?>> builder = (EnumSelectorBuilder<Enum<?>>) (Object) entryBuilder.startEnumSelector(new TranslatableText(langKey), configEntry.getType(), configEntry.getMainConfigValue());
					builder.setDefaultValue(configEntry::getDefaultValue);
					builder.setSaveConsumer(configEntry::setMainConfigValue);
					builder.setTooltip(description(langKey, configEntry));
					builder.setErrorSupplier(value -> errorSupplier(value, configEntry));
					if (requiresRestart(configEntry)) {
						builder.requireRestart(true);
					}
					return builder.build();
				}
		);
		registerEntryConverter(Float.class, (configEntry, entryBuilder, langKey) -> {
					FloatFieldBuilder builder = entryBuilder.startFloatField(new TranslatableText(langKey), configEntry.getMainConfigValue());
					builder.setDefaultValue(configEntry::getDefaultValue);
					builder.setSaveConsumer(configEntry::setMainConfigValue);
					builder.setTooltip(description(langKey, configEntry));
					builder.setErrorSupplier(value -> errorSupplier(value, configEntry));
					if (requiresRestart(configEntry)) {
						builder.requireRestart(true);
					}
					return builder.build();
				}
		);
		registerEntryConverter(Integer.class, (configEntry, entryBuilder, langKey) -> {
					IntFieldBuilder builder = entryBuilder.startIntField(new TranslatableText(langKey), configEntry.getMainConfigValue());
					builder.setDefaultValue(configEntry::getDefaultValue);
					builder.setSaveConsumer(configEntry::setMainConfigValue);
					builder.setTooltip(description(langKey, configEntry));
					builder.setErrorSupplier(value -> errorSupplier(value, configEntry));
					if (requiresRestart(configEntry)) {
						builder.requireRestart(true);
					}
					return builder.build();
				}
		);
		registerEntryConverter(String.class, (configEntry, entryBuilder, langKey) -> {
					StringFieldBuilder builder = entryBuilder.startStrField(new TranslatableText(langKey), configEntry.getMainConfigValue());
					builder.setDefaultValue(configEntry::getDefaultValue);
					builder.setSaveConsumer(configEntry::setMainConfigValue);
					builder.setTooltip(description(langKey, configEntry));
					builder.setErrorSupplier(value -> errorSupplier(value, configEntry));
					if (requiresRestart(configEntry)) {
						builder.requireRestart(true);
					}
					return builder.build();
				}
		);
	}
}
