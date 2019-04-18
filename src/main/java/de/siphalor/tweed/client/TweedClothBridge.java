package de.siphalor.tweed.client;

import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.entry.*;
import javafx.util.Pair;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.BooleanListEntry;
import me.shedaniel.cloth.gui.entries.FloatListEntry;
import me.shedaniel.cloth.gui.entries.IntegerListEntry;
import me.shedaniel.cloth.gui.entries.StringListEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.function.BiFunction;

@SuppressWarnings("unchecked")
public class TweedClothBridge {
	public static final String RESET_BUTTON_NAME = "tweed.cloth.button.reset";
	protected static final String SCREEN_NAME_PREFIX = "tweed.cloth.screen.";
	protected static final String CATEGORY_NAME_DELIMITER = ".";
	protected static final String ENTRY_NAME_DELIMITER = ".";

	protected static ArrayDeque<TweedClothBridge> modMenuBridges;
	protected static HashMap<Class, BiFunction<ConfigEntry, String, ClothConfigScreen.AbstractListEntry>> tweedEntryToClothEntry = new HashMap<>();

	protected ConfigFile configFile;
	protected String modId;
	protected ConfigScreenBuilder screenBuilder;

	public TweedClothBridge(ConfigFile configFile) {
		this.configFile = configFile;
	}

	public TweedClothBridge(ConfigFile configFile, String modId) {
		this.configFile = configFile;
		this.modId = modId;
	}

	protected void setup() {
		final String screenName = SCREEN_NAME_PREFIX + configFile.getName();

		screenBuilder = ConfigScreenBuilder.create(MinecraftClient.getInstance().currentScreen, screenName, (savedConfig) -> ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.CLIENT, ConfigScope.HIGHEST));

		ArrayDeque<Pair<String, ConfigCategory>> categories = collectCategories(configFile.getRootCategory());
		categories.add(new Pair<>("main", configFile.getRootCategory()));
		categories.forEach(pair -> {
			final String categoryName = screenName + CATEGORY_NAME_DELIMITER + pair.getKey();
			ConfigScreenBuilder.CategoryBuilder builder = screenBuilder.addCategory(categoryName);
			pair.getValue().entryStream().filter(entry -> !(entry.getValue() instanceof ConfigCategory)).forEach(entry -> {
				Class clazz = entry.getValue().getClass();
				while(clazz != Object.class) {
					if(tweedEntryToClothEntry.containsKey(clazz)) {
						builder.addOption(tweedEntryToClothEntry.get(clazz).apply(entry.getValue(), categoryName + ENTRY_NAME_DELIMITER + entry.getKey()));
						break;
					}
					clazz = clazz.getSuperclass();
				}
			});
		});
	}

	public ClothConfigScreen buildScreen(Screen parentScreen) {
		setup();
		return screenBuilder.build();
	}

	protected static ArrayDeque<Pair<String, ConfigCategory>> collectCategories(ConfigCategory configCategory) {
		ArrayDeque<Pair<String, ConfigCategory>> categories = new ArrayDeque<>();
		configCategory.entryStream().filter(entry -> entry.getValue() instanceof ConfigCategory).map(entry -> new Pair<>(entry.getKey(), (ConfigCategory) entry.getValue())).forEach(pair -> {
			categories.add(pair);
			categories.addAll(collectCategories(pair.getValue()));
		});
		return categories;
	}

	public Screen getScreen() {
		return screenBuilder.build();
	}

	public void registerForModMenu() {
		if(FabricLoader.getInstance().isModLoaded("modmenu")) {
			ModMenuWrapper.addConfigOverride(modId, () -> MinecraftClient.getInstance().openScreen(buildScreen(MinecraftClient.getInstance().currentScreen)));
		}
	}

	public static <T extends ConfigEntry> void registerClothEntryMapping(Class<T> clazz, BiFunction<T, String, ClothConfigScreen.AbstractListEntry> supplier) {
		tweedEntryToClothEntry.put(clazz, (BiFunction<ConfigEntry, String, ClothConfigScreen.AbstractListEntry>) supplier);
	}

	static {
		registerClothEntryMapping(BooleanEntry.class, (booleanEntry, key) -> new BooleanListEntry(key, booleanEntry.value, RESET_BUTTON_NAME, booleanEntry::getDefaultValue, (newVal) -> booleanEntry.value = newVal));
		registerClothEntryMapping(FloatEntry.class, (floatEntry, key) -> new FloatListEntry(key, floatEntry.value, RESET_BUTTON_NAME, floatEntry::getDefaultValue, (newVal) -> floatEntry.value = newVal));
		registerClothEntryMapping(IntEntry.class, (intEntry, key) -> new IntegerListEntry(key, intEntry.value, RESET_BUTTON_NAME, intEntry::getDefaultValue, (newVal) -> intEntry.value = newVal));
		registerClothEntryMapping(MappedEnumEntry.class, (mappedEnumEntry, key) -> new StringListEntry(key, mappedEnumEntry.writeValue((Enum) mappedEnumEntry.value).toString(), RESET_BUTTON_NAME, () -> mappedEnumEntry.writeValue((Enum) mappedEnumEntry.getDefaultValue()).toString(), mappedEnumEntry::readValue));
		registerClothEntryMapping(StringEntry.class, (stringEntry, key) -> new StringListEntry(key, stringEntry.value, RESET_BUTTON_NAME, stringEntry::getDefaultValue, (newVal) -> stringEntry.value = newVal));
	}
}
