package de.siphalor.tweed.modules;

import de.siphalor.tweed.client.TweedClothBridge;
import de.siphalor.tweed.config.ConfigCategory;
import de.siphalor.tweed.config.ConfigFile;
import de.siphalor.tweed.config.TweedRegistry;
import de.siphalor.tweed.data.serializer.ConfigDataSerializer;
import de.siphalor.tweed.modules.api.MainModule;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ModuleManager {
    protected final String modId;
    protected final ConfigDataSerializer configDataSerializer;
    protected final boolean multiFile;
    protected TweedClothBridge tweedClothBridge;

    protected ConcurrentLinkedQueue<ConfigFile> configFiles = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<MainModule> modules = new ConcurrentLinkedQueue<>();

    public ModuleManager(String modId, ConfigDataSerializer configDataSerializer, boolean multiFile) {
        this.modId = modId;
        this.configDataSerializer = configDataSerializer;
        this.multiFile = multiFile;
        if(!multiFile) {
            configFiles.add(TweedRegistry.registerConfigFile(modId, configDataSerializer));
        }
    }

    public void registerModules(MainModule... newModules) {
        modules.addAll(Arrays.asList(newModules));

        if(multiFile) {
            Arrays.stream(newModules).forEach(mainModule -> {
                ConfigFile configFile = TweedRegistry.registerConfigFile(modId + "/" + mainModule.getId(), configDataSerializer);
                configFiles.add(configFile);
                mainModule.setRootCategory(configFile.getRootCategory());
                mainModule.setup();
            });
        } else {
            Arrays.stream(newModules).forEach(mainModule -> {
                //noinspection ConstantConditions
                mainModule.setRootCategory(configFiles.peek().register(mainModule.getId(), new ConfigCategory()));
            });
        }
    }

    public void setup() {
        tweedClothBridge = new TweedClothBridge(modId, configFiles.toArray(new ConfigFile[0]));
    }

    public Screen buildConfigScreen() {
        return tweedClothBridge.buildScreen();
    }

    public ConcurrentLinkedQueue<MainModule> getModules() {
        return modules;
    }
}