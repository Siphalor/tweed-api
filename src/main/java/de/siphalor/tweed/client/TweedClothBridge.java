package de.siphalor.tweed.client;

import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.constraints.Constraint;
import de.siphalor.tweed.config.constraints.RangeConstraint;
import de.siphalor.tweed.config.entry.*;
import io.netty.buffer.Unpooled;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
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
	protected static HashMap<Class, BiFunction<ConfigEntry, String, AbstractConfigListEntry>> tweedEntryToClothEntry = new HashMap<>();

	protected ConfigFileEntry[] configFiles;
	boolean openingScheduled = false;
	protected String id;
	protected String screenId;
	protected ConfigBuilder screenBuilder;
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
				ClientSidePacketRegistry.INSTANCE.sendToServer(Tweed.REQUEST_SYNC_C2S_PACKET, buffer);
                entry.awaitSync = true;
			}
            TweedClient.scheduledClothBridge = this;

			return new NoticeScreen(
				() -> {
					MinecraftClient.getInstance().openScreen(parentScreen);
                    TweedClient.scheduledClothBridge = null;
				},
				new TranslatableText("tweed.gui.screen.syncFromServer"),
				new TranslatableText("tweed.gui.screen.syncFromServer.note")
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
        	TweedClient.scheduledClothBridge = null;
			MinecraftClient.getInstance().openScreen(buildScreen());
		}
	}

	public Screen buildScreen() {
		screenId = SCREEN_NAME_PREFIX + id;

		screenBuilder = createScreenBuilder();
		screenBuilder.setParentScreen(parentScreen);
		screenBuilder.setSavingRunnable(this::save);
		screenBuilder.setTitle(screenId);

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

	protected ConfigBuilder createScreenBuilder() {
		return ConfigBuilder.create();
	}

	protected void addCategory(String name, ConfigCategory configCategory) {
		final String categoryName;
		if(configFiles.length > 1)
			categoryName = SCREEN_NAME_PREFIX + name;
		else
			categoryName = screenId + CATEGORY_NAME_DELIMITER + name;
		me.shedaniel.clothconfig2.api.ConfigCategory category = screenBuilder.getOrCreateCategory(categoryName);
		if(configCategory.getBackgroundTexture() != null) {
			category.setCategoryBackground(configCategory.getBackgroundTexture());
		}
		category.addEntry(new TextListEntry(categoryName, configCategory.getCleanedDescription(), Color.LIGHT_GRAY.getRGB()));
		configCategory.entryStream().forEach(entry -> addOption(category, categoryName + ENTRY_NAME_DELIMITER + entry.getKey(), entry.getValue()));
	}

	protected void addOption(me.shedaniel.clothconfig2.api.ConfigCategory categoryBuilder, String name, ConfigEntry configEntry) {
		categoryBuilder.addEntry(getClothEntry(configEntry, name));
	}

	public static <T extends ConfigEntry> void registerClothEntryMapping(Class<T> clazz, BiFunction<T, String, AbstractConfigListEntry> supplier) {
		tweedEntryToClothEntry.put(clazz, (BiFunction<ConfigEntry, String, AbstractConfigListEntry>) supplier);
	}

	public static AbstractConfigListEntry getClothEntry(ConfigEntry configEntry, String name) {
		AbstractConfigListEntry listEntry = null;

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
			(intEntry, key) -> {
				Optional<Constraint<Integer>> optionalConstraint = intEntry.getPostConstraints().stream().filter(integerConstraint -> integerConstraint instanceof RangeConstraint).findAny();
				if(optionalConstraint.isPresent() && ((RangeConstraint) optionalConstraint.get()).hasRealBounds()) {
					RangeConstraint<Integer> rangeConstraint = (RangeConstraint<Integer>) optionalConstraint.get();
					return new IntegerSliderEntry(key, rangeConstraint.getMin(), rangeConstraint.getMax(), intEntry.getMainConfigValue(), RESET_BUTTON_NAME,
						intEntry::getDefaultValue,
						intEntry::setMainConfigValue,
						intEntry::getClothyDescription
					);
				}
				return new IntegerListEntry(key, intEntry.getMainConfigValue(), RESET_BUTTON_NAME,
					intEntry::getDefaultValue,
					intEntry::setMainConfigValue,
					intEntry::getClothyDescription
				);
			}
		);
		registerClothEntryMapping(EnumEntry.class,
			(enumEntry, key) -> new EnumListEntry(key, enumEntry.getDefaultValue().getClass(), (Enum) enumEntry.getMainConfigValue(), RESET_BUTTON_NAME,
                enumEntry::getDefaultValue,
				enumEntry::setMainConfigValue,
				Object::toString,
				enumEntry::getClothyDescription
			));
		registerClothEntryMapping(MappedEnumEntry.class,
			(mappedEnumEntry, key) -> new StringListEntry(key, mappedEnumEntry.getValue((Enum) mappedEnumEntry.value), RESET_BUTTON_NAME,
				() -> mappedEnumEntry.getValue((Enum) mappedEnumEntry.getDefaultValue()),
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
				List<AbstractConfigListEntry> entries = new ArrayList<>();
				if(!categoryEntry.getDescription().isEmpty())
					entries.add(new TextListEntry(key, categoryEntry.getCleanedDescription(), Color.LIGHT_GRAY.getRGB()));
				entries.addAll(categoryEntry.entryStream().map(entry -> getClothEntry(entry.getValue(), key + CATEGORY_NAME_DELIMITER + entry.getKey())).collect(Collectors.toList()));
				return new SubCategoryListEntry(key, entries, false);
			}
		);
	}

	private void save() {
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
