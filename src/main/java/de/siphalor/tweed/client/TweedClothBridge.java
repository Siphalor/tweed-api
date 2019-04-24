package de.siphalor.tweed.client;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.entry.*;
import de.siphalor.tweed.util.Recursive;
import io.netty.buffer.Unpooled;
import javafx.util.Pair;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.*;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.NoticeScreen;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.PacketByteBuf;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class TweedClothBridge {
	public static final String RESET_BUTTON_NAME = "tweed.cloth.button.reset";
	protected static final String SCREEN_NAME_PREFIX = "tweed.cloth.";
	protected static final String CATEGORY_NAME_DELIMITER = ".";
	protected static final String ENTRY_NAME_DELIMITER = ".";

	protected static ArrayDeque<TweedClothBridge> tweedClothBridges = new ArrayDeque<>();
	protected static ArrayDeque<TweedClothBridge> modMenuBridges = new ArrayDeque<>();
	protected static HashMap<Class, BiFunction<ConfigEntry, String, ClothConfigScreen.AbstractListEntry>> tweedEntryToClothEntry = new HashMap<>();

	protected ConfigFile configFile;
	protected String modId;
	protected String screenId;
	protected ConfigScreenBuilder screenBuilder;
	protected boolean awaitSync = false;
	protected Screen parentScreen;
	protected boolean inGame = false;

	public TweedClothBridge(ConfigFile configFile) {
		this.configFile = configFile;
		tweedClothBridges.add(this);
	}

	public TweedClothBridge(ConfigFile configFile, String modId) {
		this(configFile);
		this.modId = modId;
	}

	public void open() {
        inGame = MinecraftClient.getInstance().world != null;
		parentScreen = MinecraftClient.getInstance().currentScreen;

        if(inGame) {
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
			buffer.writeString(configFile.getName());
			buffer.writeEnumConstant(ConfigEnvironment.UNIVERSAL);
			buffer.writeEnumConstant(ConfigScope.SMALLEST);
			ClientSidePacketRegistry.INSTANCE.sendToServer(Core.REQUEST_SYNC_C2S_PACKET, buffer);
			awaitSync = true;

			MinecraftClient.getInstance().openScreen(new NoticeScreen(
				() -> {
					MinecraftClient.getInstance().openScreen(parentScreen);
					awaitSync = false;
				},
				new TranslatableTextComponent("tweed.gui.screen.syncFromServer"),
				new TranslatableTextComponent("tweed.gui.screen.syncFromServer.note")
			));
		} else {
        	MinecraftClient.getInstance().openScreen(buildScreen());
		}
	}

	public void onSync() {
		if(awaitSync) {
			awaitSync = false;
			MinecraftClient.getInstance().openScreen(buildScreen());

			Recursive<Consumer<Map.Entry<String, ConfigEntry>>> recursive = new Recursive<>();
			recursive.lambda = entry -> {
				if(entry.getValue().getEnvironment() != ConfigEnvironment.CLIENT) {
					if(entry.getValue() instanceof ConfigCategory) {
						((ConfigCategory) entry.getValue()).entryStream().forEach(recursive.lambda);
					} else if(entry.getValue() instanceof AbstractValueEntry) {
						((AbstractValueEntry) entry.getValue()).setMainConfigValue(((AbstractValueEntry) entry.getValue()).value);
					}
				}
			};
			configFile.getRootCategory().entryStream().filter(entry -> entry.getValue().getEnvironment() != ConfigEnvironment.CLIENT).forEach(recursive.lambda);
		}
	}

	public ClothConfigScreen buildScreen() {
		screenId = SCREEN_NAME_PREFIX + configFile.getName();

		screenBuilder = ConfigScreenBuilder.create(parentScreen, screenId, this::save);

		// setup
		if(configFile.getRootCategory().entryStream().anyMatch(entry -> !(entry.getValue() instanceof ConfigCategory))) {
			addCategory("main", configFile.getRootCategory());
		} else {
			configFile.getRootCategory().entryStream().filter(entry -> entry.getValue() instanceof ConfigCategory).forEach(entry -> addCategory(entry.getKey(), (ConfigCategory) entry.getValue()));
		}

		// build
		return screenBuilder.build();
	}

	protected void addCategory(String name, ConfigCategory configCategory) {
		final String categoryName = screenId + CATEGORY_NAME_DELIMITER + name;
		ConfigScreenBuilder.CategoryBuilder categoryBuilder = screenBuilder.addCategory(categoryName);
		categoryBuilder.addOption(new TextListEntry(categoryName, configCategory.getCleanedDescription(), Color.LIGHT_GRAY.getRGB()));
		configCategory.entryStream().forEach(entry -> addOption(categoryBuilder, categoryName + ENTRY_NAME_DELIMITER + entry.getKey(), entry.getValue()));
	}

	protected void addOption(ConfigScreenBuilder.CategoryBuilder categoryBuilder, String name, ConfigEntry configEntry) {
		categoryBuilder.addOption(getClothEntry(configEntry, name));
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
			ModMenuWrapper.addConfigOverride(modId, this::open);
		}
	}

	public static <T extends ConfigEntry> void registerClothEntryMapping(Class<T> clazz, BiFunction<T, String, ClothConfigScreen.AbstractListEntry> supplier) {
		tweedEntryToClothEntry.put(clazz, (BiFunction<ConfigEntry, String, ClothConfigScreen.AbstractListEntry>) supplier);
	}

	public static ClothConfigScreen.AbstractListEntry getClothEntry(ConfigEntry configEntry, String name) {
		ClothConfigScreen.AbstractListEntry listEntry = null;

        Class clazz = configEntry.getClass();
        while(clazz != ConfigEntry.class) {
        	if(tweedEntryToClothEntry.containsKey(clazz)) {
        		listEntry = tweedEntryToClothEntry.get(clazz).apply(configEntry, name);
        		break;
			}
        	clazz = clazz.getSuperclass();
		}

        return listEntry;
	}

	static {
		registerClothEntryMapping(BooleanEntry.class,
			(booleanEntry, key) -> new BooleanListEntry(
				key, booleanEntry.getMainConfigValue(), RESET_BUTTON_NAME,
				booleanEntry::getDefaultValue,
                booleanEntry::setMainConfigValue,
				booleanEntry::getClothyDescription
			));
		registerClothEntryMapping(FloatEntry.class,
			(floatEntry, key) -> new FloatListEntry(key, floatEntry.getMainConfigValue(), RESET_BUTTON_NAME,
				floatEntry::getDefaultValue,
                floatEntry::setMainConfigValue,
				floatEntry::getClothyDescription
			));
		registerClothEntryMapping(IntEntry.class,
			(intEntry, key) -> new IntegerListEntry(key, intEntry.getMainConfigValue(), RESET_BUTTON_NAME,
				intEntry::getDefaultValue,
                intEntry::setMainConfigValue,
				intEntry::getClothyDescription
			));
		registerClothEntryMapping(MappedEnumEntry.class,
			(mappedEnumEntry, key) -> new StringListEntry(key, mappedEnumEntry.writeValue((Enum) mappedEnumEntry.getMainConfigValue()).toString(), RESET_BUTTON_NAME,
				() -> mappedEnumEntry.writeValue((Enum) mappedEnumEntry.getDefaultValue()).toString(),
                newVal -> mappedEnumEntry.setMainConfigValue(mappedEnumEntry.getValue(newVal)),
				mappedEnumEntry::getClothyDescription
			));
		registerClothEntryMapping(StringEntry.class,
			(stringEntry, key) -> new StringListEntry(key, stringEntry.getMainConfigValue(), RESET_BUTTON_NAME,
				stringEntry::getDefaultValue,
                stringEntry::setMainConfigValue,
				stringEntry::getClothyDescription
			));

		registerClothEntryMapping(ConfigCategory.class,
			(categoryEntry, key) -> {
				List<ClothConfigScreen.AbstractListEntry> entries = new ArrayList<>(Collections.singleton(new TextListEntry(key, categoryEntry.getCleanedDescription(), Color.LIGHT_GRAY.getRGB())));
				entries.addAll(categoryEntry.entryStream().map(entry -> getClothEntry(entry.getValue(), key + CATEGORY_NAME_DELIMITER + entry.getKey())).collect(Collectors.toList()));
				return new SubCategoryListEntry(key, entries, false);
			}
		);
	}

	private void save(ConfigScreenBuilder.SavedConfig savedConfig) {
		if(inGame) {
			configFile.syncToServer(ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
            ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.CLIENT, ConfigScope.HIGHEST);
		} else {
            ConfigLoader.updateMainConfigFile(configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
		}
	}
}
