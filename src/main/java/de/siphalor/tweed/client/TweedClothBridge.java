package de.siphalor.tweed.client;

import de.siphalor.tweed.Core;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.entry.*;
import io.netty.buffer.Unpooled;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.*;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.NoticeScreen;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.PacketByteBuf;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class TweedClothBridge {
	public static final String RESET_BUTTON_NAME = "tweed.cloth.button.reset";
	protected static final String SCREEN_NAME_PREFIX = "tweed.cloth.";
	protected static final String CATEGORY_NAME_DELIMITER = ".";
	protected static final String ENTRY_NAME_DELIMITER = ".";

	protected static Queue<TweedClothBridge> tweedClothBridges = new ConcurrentLinkedQueue<>();
	protected static HashMap<Class, BiFunction<ConfigEntry, String, ClothConfigScreen.AbstractListEntry>> tweedEntryToClothEntry = new HashMap<>();

	protected ConfigFileEntry[] configFiles;
	boolean openingScheduled = false;
	protected String id;
	protected String screenId;
	protected ConfigScreenBuilder screenBuilder;
	protected Screen parentScreen;
	protected boolean inGame = false;

	public TweedClothBridge(ConfigFile configFile) {
		this(configFile.getName(), configFile);
	}

	public TweedClothBridge(String id, ConfigFile... configFiles) {
		this.id = id;
		this.configFiles = Arrays.stream(configFiles).map(file -> new ConfigFileEntry(file, false)).toArray(ConfigFileEntry[]::new);
		tweedClothBridges.add(this);
	}

	public Screen open() {
        inGame = MinecraftClient.getInstance().world != null;
		parentScreen = MinecraftClient.getInstance().currentScreen;

        if(inGame) {
        	boolean requiresSync = false;
            for(ConfigFileEntry entry : configFiles) {
            	if(entry.configFile.getRootCategory().entryStream().anyMatch(configEntry -> configEntry.getValue().getEnvironment() != ConfigEnvironment.CLIENT)) {
            		requiresSync = true;
            		break;
				}
			}
            if(!requiresSync) {
                return buildScreen();
			}

        	for(ConfigFileEntry entry : configFiles) {
				PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
				buffer.writeString(entry.configFile.getName());
				buffer.writeEnumConstant(ConfigEnvironment.UNIVERSAL);
				buffer.writeEnumConstant(ConfigScope.SMALLEST);
				buffer.writeEnumConstant(ConfigOrigin.MAIN);
				ClientSidePacketRegistry.INSTANCE.sendToServer(Core.REQUEST_SYNC_C2S_PACKET, buffer);
                entry.awaitSync = true;
			}
            ClientCore.scheduledClothBridge = this;

			return new NoticeScreen(
				() -> {
					MinecraftClient.getInstance().openScreen(parentScreen);
                    ClientCore.scheduledClothBridge = null;
				},
				new TranslatableTextComponent("tweed.gui.screen.syncFromServer"),
				new TranslatableTextComponent("tweed.gui.screen.syncFromServer.note")
			);
		} else {
            return buildScreen();
		}
	}

	public void onSync(ConfigFile configFile) {
        for(ConfigFileEntry entry : configFiles) {
        	if(entry.configFile == configFile) {
        		if(entry.awaitSync) {
        			entry.awaitSync = false;
        			break;
				}
			}
		}
        if(Arrays.stream(configFiles).noneMatch(entry -> entry.awaitSync)) {
        	ClientCore.scheduledClothBridge = null;
			MinecraftClient.getInstance().openScreen(buildScreen());
		}
	}

	public ClothConfigScreen buildScreen() {
		screenId = SCREEN_NAME_PREFIX + id;

		screenBuilder = createScreenBuilder();

		// setup
		if(configFiles.length > 1) {
			Arrays.stream(configFiles).forEach(entry -> addCategory(entry.configFile.getName(), entry.configFile.getRootCategory()));
		} else {
			if (configFiles[0].configFile.getRootCategory().entryStream().anyMatch(entry -> !(entry.getValue() instanceof ConfigCategory))) {
				addCategory("main", configFiles[0].configFile.getRootCategory());
			} else {
				configFiles[0].configFile.getRootCategory().entryStream().forEach(entry -> addCategory(entry.getKey(), (ConfigCategory) entry.getValue()));
			}
		}

		// build
		return screenBuilder.build();
	}

	protected ConfigScreenBuilder createScreenBuilder() {
		return ConfigScreenBuilder.create(parentScreen, screenId, this::save);
	}

	protected void addCategory(String name, ConfigCategory configCategory) {
		final String categoryName;
		if(configFiles.length > 1)
			categoryName = SCREEN_NAME_PREFIX + name;
		else
			categoryName = screenId + CATEGORY_NAME_DELIMITER + name;
		ConfigScreenBuilder.CategoryBuilder categoryBuilder = screenBuilder.addCategory(categoryName);
		if(configCategory.getBackgroundTexture() != null) {
			categoryBuilder.setBackgroundTexture(configCategory.getBackgroundTexture());
		}
		categoryBuilder.addOption(new TextListEntry(categoryName, configCategory.getCleanedDescription(), Color.LIGHT_GRAY.getRGB()));
		configCategory.sortedEntryStream().forEach(entry -> addOption(categoryBuilder, categoryName + ENTRY_NAME_DELIMITER + entry.getKey(), entry.getValue()));
	}

	protected void addOption(ConfigScreenBuilder.CategoryBuilder categoryBuilder, String name, ConfigEntry configEntry) {
		categoryBuilder.addOption(getClothEntry(configEntry, name));
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
		registerClothEntryMapping(EnumEntry.class,
			(enumEntry, key) -> new EnumListEntry(key, enumEntry.getDefaultValue().getClass(), (Enum) enumEntry.getMainConfigValue(), RESET_BUTTON_NAME,
                enumEntry::getDefaultValue,
				enumEntry::setMainConfigValue,
				Object::toString,
				enumEntry::getClothyDescription
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
				entries.addAll(categoryEntry.sortedEntryStream().map(entry -> getClothEntry(entry.getValue(), key + CATEGORY_NAME_DELIMITER + entry.getKey())).collect(Collectors.toList()));
				return new SubCategoryListEntry(key, entries, false);
			}
		);
	}

	private void save(ConfigScreenBuilder.SavedConfig savedConfig) {
		Arrays.stream(configFiles).forEach(entry -> {
			if(inGame) {
				entry.configFile.syncToServer(ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
				ConfigLoader.updateMainConfigFile(entry.configFile, ConfigEnvironment.CLIENT, ConfigScope.HIGHEST);
                ConfigLoader.loadConfigs(MinecraftClient.getInstance().getResourceManager(), ConfigEnvironment.CLIENT, ConfigScope.SMALLEST);
			} else {
				ConfigLoader.updateMainConfigFile(entry.configFile, ConfigEnvironment.UNIVERSAL, ConfigScope.HIGHEST);
				ConfigLoader.loadConfigs(MinecraftClient.getInstance().getResourceManager(), ConfigEnvironment.UNIVERSAL, ConfigScope.WORLD);
			}
		});
	}

	protected static class ConfigFileEntry {
		public ConfigFile configFile;
		public boolean awaitSync;

		public ConfigFileEntry(ConfigFile configFile, boolean awaitSync) {
			this.configFile = configFile;
			this.awaitSync = awaitSync;
		}
	}
}
