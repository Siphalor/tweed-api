package de.siphalor.tweed.tailor;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.config.entry.ConfigEntry;
import de.siphalor.tweed.config.entry.ValueConfigEntry;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ClothTailor extends Tailor {
	public static final ClothTailor INSTANCE = new ClothTailor();

	protected static final String SCREEN_NAME_PREFIX = "tweed.cloth.";

	private static final Map<Class<?>, EntryConverter<?>> ENTRY_CONVERTERS = new HashMap<>();
	private Map<String, ConfigScreenFactory<?>> screenFactories = new HashMap<>();

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
		if (inGame) {
			return null;
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

		if (configFile.getRootCategory().entryStream().allMatch(entry -> entry instanceof ConfigCategory)) {
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
				categoryBuilder.setTooltipSupplier(entry.getValue()::getClothyDescription);

				convertCategory(entryBuilder, categoryBuilder::add, (ConfigCategory) entry.getValue(), subPath);

				registry.accept(categoryBuilder.build());

			} else if (entry.getValue() instanceof ValueConfigEntry<?, ?>) {
				Class<?> clazz = ((ValueConfigEntry<?, ?>) entry.getValue()).getType();
				EntryConverter<?> entryConverter = null;

				entryConverter = ENTRY_CONVERTERS.get(clazz);
				while(clazz != Object.class && entryConverter == null) {
					clazz = clazz.getSuperclass();
					entryConverter = ENTRY_CONVERTERS.get(clazz);
				}

				if (entryConverter != null) {
					registry.accept(entryConverter.convert((ValueConfigEntry) entry.getValue(), entryBuilder, subPath));
				} else {
					Tweed.LOGGER.warn("Couldn't convert config entry of type " + ((ValueConfigEntry<?, ?>) entry.getValue()).getType().getSimpleName() + " to cloth entry!");
				}
			}
		});
	}

	private void createCategory(ConfigBuilder configBuilder, ConfigCategory configCategory, String name) {
		me.shedaniel.clothconfig2.api.ConfigCategory clothCategory = configBuilder.getOrCreateCategory(new TranslatableText(name));
		if (configCategory.getBackgroundTexture() != null) {
			clothCategory.setCategoryBackground(configCategory.getBackgroundTexture());
		}
		convertCategory(configBuilder.entryBuilder(), clothCategory::addEntry, configCategory, name);
	}

	private void save(ConfigFile configFile) {
		if (MinecraftClient.getInstance().world != null) {

		} else {
			ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
			ConfigLoader.loadConfigs(MinecraftClient.getInstance().getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD);
		}
	}

	public static <V> void registerEntryConverter(Class<V> valueType, EntryConverter<V> converter) {
		ENTRY_CONVERTERS.put(valueType, converter);
	}

	public static <V> Optional<Text> errorSupplier(V value, ValueConfigEntry<V, ?> configEntry) {
		try {
			configEntry.applyConstraints(value);
		} catch (ConstraintException e) {
			return Optional.of(new LiteralText(e.getMessage()));
		}
		return Optional.empty();
	}

	@FunctionalInterface
	public interface EntryConverter<V> {
		AbstractConfigListEntry<?> convert(ValueConfigEntry<V, ?> configEntry, ConfigEntryBuilder entryBuilder, String langKey);
	}

	static {
		registerEntryConverter(Boolean.class, (configEntry, entryBuilder, langKey) ->
				entryBuilder.startBooleanToggle(new TranslatableText(langKey), configEntry.getMainConfigValue())
						.setDefaultValue(configEntry::getDefaultValue)
						.setSaveConsumer(configEntry::setMainConfigValue)
						.setTooltipSupplier(configEntry::getClothyDescription)
						.setErrorSupplier(value -> errorSupplier(value, configEntry))
						.build()
		);
		registerEntryConverter(Enum.class, (configEntry, entryBuilder, langKey) ->
				entryBuilder.startEnumSelector(new TranslatableText(langKey), configEntry.getType(), configEntry.getMainConfigValue())
						.setDefaultValue(configEntry::getDefaultValue)
						.setSaveConsumer(configEntry::setMainConfigValue)
						.setTooltipSupplier(configEntry::getClothyDescription)
						.setErrorSupplier(value -> errorSupplier(value, configEntry))
						.build()
		);
		registerEntryConverter(Float.class, (configEntry, entryBuilder, langKey) ->
				entryBuilder.startFloatField(new TranslatableText(langKey), configEntry.getMainConfigValue())
						.setDefaultValue(configEntry::getDefaultValue)
						.setSaveConsumer(configEntry::setMainConfigValue)
						.setTooltipSupplier(configEntry::getClothyDescription)
						.setErrorSupplier(value -> errorSupplier(value, configEntry))
						.build()
		);
		registerEntryConverter(Integer.class, (configEntry, entryBuilder, langKey) ->
				entryBuilder.startIntField(new TranslatableText(langKey), configEntry.getMainConfigValue())
						.setDefaultValue(configEntry::getDefaultValue)
						.setSaveConsumer(configEntry::setMainConfigValue)
						.setTooltipSupplier(configEntry::getClothyDescription)
						.setErrorSupplier(value -> errorSupplier(value, configEntry))
						.build()
		);
		registerEntryConverter(String.class, (configEntry, entryBuilder, langKey) ->
				entryBuilder.startStrField(new TranslatableText(langKey), configEntry.getMainConfigValue())
						.setDefaultValue(configEntry::getDefaultValue)
						.setSaveConsumer(configEntry::setMainConfigValue)
						.setTooltipSupplier(configEntry::getClothyDescription)
						.setErrorSupplier(value -> errorSupplier(value, configEntry))
						.build()
		);
	}
}
