/*
 * Copyright 2021 Siphalor
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

package de.siphalor.tweed4.config;

import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.config.entry.ConfigEntry;
import de.siphalor.tweed4.config.fixers.ConfigEntryFixer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import de.siphalor.tweed4.data.serializer.ConfigDataSerializer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resource.Resource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

/**
 * A configuration file.
 * @see TweedRegistry#registerConfigFile(String, ConfigDataSerializer)
 */
@SuppressWarnings("unused")
public class ConfigFile {
	private String name;
	private BiConsumer<ConfigEnvironment, ConfigScope> reloadListener = null;
	private Queue<Pair<String, ConfigEntryFixer>> configEntryFixers;
	private ConfigDataSerializer<?, ?, ?> dataSerializer;

	private Map<Class<? extends Annotation>, Annotation> tailorAnnotations;

	protected ConfigCategory rootCategory;

	protected ConfigFile(String name, ConfigDataSerializer<?, ?, ?> dataSerializer) {
		this(name, dataSerializer, new ConfigCategory());
	}

	@ApiStatus.Internal
	public ConfigFile(String name, ConfigDataSerializer<?, ?, ?> dataSerializer, ConfigCategory rootCategory) {
		this.name = name;
		this.rootCategory = rootCategory;
		this.dataSerializer = dataSerializer;
		configEntryFixers = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Adds a new reload listener.
	 *
	 * This gets called after all reloading of sub-entries is done for the specific reload point.
	 * @param listener a {@link BiConsumer} accepting the used {@link ConfigEnvironment} and {@link ConfigScope}
	 * @return the current config file (for chain calls)
	 */
	public ConfigFile setReloadListener(BiConsumer<ConfigEnvironment, ConfigScope> listener) {
		reloadListener = listener;
		return this;
	}

	public  <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	ConfigDataSerializer<V, L, O> getDataSerializer() {
		//noinspection unchecked
		return (ConfigDataSerializer<V, L, O>) dataSerializer;
	}

	public void finishReload(ConfigEnvironment environment, ConfigScope scope) {
		Tweed.LOGGER.info("Reloaded configs for " + name + " (" + environment.name().toLowerCase(Locale.ENGLISH) + "/" + scope.name().toLowerCase(Locale.ENGLISH) + ")");
		if(reloadListener != null)
			reloadListener.accept(environment, scope);
	}

	/**
	 * Gets the file identifier used in datapacks.
	 * @return the identifier
	 */
	public Identifier getFileIdentifier() {
		return new Identifier(Tweed.MOD_ID, "config/" + getFileName());
	}

	/**
	 * Gets the file id
	 * @return the file id <b>with extension</b>
	 * @see ConfigFile#getName()
	 */
	public String getFileName() {
		return name + "." + dataSerializer.getFileExtension();
	}

	/**
	 * Gets the id
	 * @return the (file) id <b>without extension</b>
	 * @see ConfigFile#getFileName()
	 */
	public String getName() {
		return name.replace('/', '.');
	}

	public ConfigCategory getRootCategory() {
		return rootCategory;
	}

	/**
	 * Sets the root category. <b>Use with extreme caution!</b>
	 * @param rootCategory the new root category
	 */
	public void setRootCategory(ConfigCategory rootCategory) {
		this.rootCategory = rootCategory;
	}

	/**
	 * Registers a new {@link ConfigEntry}.
	 * @param name the property id or path of the entry ({@link Tweed#PATH_DELIMITER}
	 * @param entry the entry itself
	 * @return the entry (for chain calls) or <i>null</i> if the path to the entry is invalid
	 */
	public <T extends ConfigEntry<?>> T register(String name, T entry) {
        String[] parts = StringUtils.split(name, Tweed.PATH_DELIMITER);
        if(parts.length == 1)
        	rootCategory.register(name, entry);
        else {
        	ConfigCategory category = rootCategory;
        	for(int i = 0; i < parts.length - 1; i++) {
        		ConfigEntry<?> iEntry = category.entries.get(parts[i]);
                if(!(iEntry instanceof ConfigCategory)) {
                	return null;
				}
				category = (ConfigCategory) iEntry;
			}
        	category.register(parts[parts.length - 1], entry);
		}
		return entry;
	}

	/**
	 * Registers a new {@link ConfigEntryFixer}
	 * @param path the id/path of the value to be fixed
	 * @param configEntryFixer a fixer
	 */
	public void register(String path, ConfigEntryFixer configEntryFixer) {
		configEntryFixers.add(new Pair<>(path, configEntryFixer));
	}

	/**
	 * Writes to the {@link DataObject} for handing it to the {@link Tweed#mainConfigDirectory}
	 *
	 * @param dataObject the target data
	 * @param environment the current environment
	 * @param scope the current definition scope
	 * @return the new {@link DataObject}
	 */
	public  <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	O write(O dataObject, ConfigEnvironment environment, ConfigScope scope) {
		fixConfig(dataObject);
		rootCategory.write(dataObject, "", environment, scope);
		return dataObject;
	}

	/**
	 * Resets all entries to their default values
	 * @param environment The current {@link ConfigEnvironment}
	 * @param scope The current {@link ConfigScope}
	 */
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
        rootCategory.reset(environment, scope);
	}

	public <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void fixConfig(O dataObject) {
		configEntryFixers.forEach(stringConfigEntryFixerPair -> {
			String entryName;
			O location;
			if (stringConfigEntryFixerPair.getLeft().isEmpty()) {
				entryName = "";
				location = dataObject;
			} else {
				String[] parts = StringUtils.split(stringConfigEntryFixerPair.getLeft(), Tweed.PATH_DELIMITER);
				location = dataObject;
				for (int i = 0; i < parts.length - 1; i++) {
					V dataValue = location.get(parts[i]);
					if (dataValue == null || !dataValue.isObject())
						return;
					location = dataValue.asObject();
				}
				entryName = parts[parts.length - 1];
			}
			stringConfigEntryFixerPair.getRight().fix(location, entryName, dataObject);
		});
	}

	public <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void load(Resource resource, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		O dataObject = this.<V, L, O>getDataSerializer().read(resource.getInputStream());
		try {
			resource.close();
		} catch (IOException e) {
			Tweed.LOGGER.error("Failed to close config resource after reading it in resource pack: " + resource.getResourcePackName());
			e.printStackTrace();
		}
		if(dataObject != null) {
			load(dataObject, environment, scope, origin);
		}
	}

	public <V extends DataValue<V, L, O>, L extends DataList<V, L ,O>, O extends DataObject<V, L, O>>
	void load(DataObject<V, L, O> dataObject, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		this.fixConfig(dataObject.asObject());

		try {
			//noinspection unchecked
			rootCategory.read((V) dataObject, environment, scope, origin);
		} catch (ConfigReadException e) {
            Tweed.LOGGER.error("The config file " + name + "." + dataSerializer.getFileExtension() + " must contain an object!");
		}
	}

	public void syncToClients(ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeEnumConstant(origin);
		packetByteBuf.writeString(name);
		write(packetByteBuf, environment, scope, origin);

		for (MinecraftServer server : Tweed.MINECRAFT_SERVERS) {
			for (ServerPlayerEntity player : PlayerLookup.all(server)) {
				ServerPlayNetworking.send(player, Tweed.CONFIG_SYNC_S2C_PACKET, packetByteBuf);
			}
		}
	}

	public void syncToClient(ServerPlayerEntity playerEntity, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeEnumConstant(origin);
		packetByteBuf.writeString(name);
		write(packetByteBuf, environment, scope, origin);

		ServerPlayNetworking.send(playerEntity, Tweed.CONFIG_SYNC_S2C_PACKET, packetByteBuf);
	}

	public void syncToServer(ConfigEnvironment environment, ConfigScope scope) {
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeString(name);
		packetByteBuf.writeEnumConstant(environment);
		packetByteBuf.writeEnumConstant(scope);
		write(packetByteBuf, environment, scope, ConfigOrigin.MAIN);

		ClientPlayNetworking.send(Tweed.TWEED_CLOTH_SYNC_C2S_PACKET, packetByteBuf);
	}

	protected void write(PacketByteBuf buffer, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		rootCategory.write(buffer, environment, scope, origin);
	}

	public void read(PacketByteBuf buffer, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		rootCategory.read(buffer, environment, scope, origin);
		if(reloadListener != null)
			reloadListener.accept(environment, scope);
	}

	/**
	 * Convenience function for <code>getRootCategory().setBackgroundTexture(...)</code>.
	 * @param path the resource path to the background texture
	 * @see ConfigCategory#setBackgroundTexture(Identifier)
	 */
	public ConfigFile setBackgroundTexture(Identifier path) {
		rootCategory.setBackgroundTexture(path);
		return this;
	}

	/**
	 * Convenience function for <code>getRootCategory().setComment(...)</code>.
	 * @param comment the comment
	 * @see ConfigCategory#setComment(String)
	 */
	public ConfigFile setComment(String comment) {
		rootCategory.setComment(comment);
		return this;
	}

	/**
	 * Sets the default environment for config entries. Equivalent to <code>getRootCategory().setEnvironment(...)</code>
	 * @param environment the environment
	 */
	public ConfigFile setEnvironment(ConfigEnvironment environment) {
		rootCategory.setEnvironment(environment);
		return this;
	}

	/**
	 * Sets the default scope for config entries. Equivalent to <code>getRootCategory().setScope(...)</code>
	 * @param scope the scope
	 */
	public ConfigFile setScope(ConfigScope scope) {
		rootCategory.setScope(scope);
		return this;
	}

	public void addTailorData(Annotation annotation) {
		if (tailorAnnotations == null) {
			tailorAnnotations = new HashMap<>();
		}
		tailorAnnotations.put(annotation.annotationType(), annotation);
	}

	public void addTailorAnnotations(Collection<Annotation> annotations) {
		if (tailorAnnotations == null) {
			tailorAnnotations = new HashMap<>();
		}
		annotations.forEach(a -> tailorAnnotations.put(a.annotationType(), a));
	}

	public void addTailorAnnotations(Annotation... annotations) {
		if (tailorAnnotations == null) {
			tailorAnnotations = new HashMap<>();
		}
		for (Annotation a : annotations) {
			tailorAnnotations.put(a.annotationType(), a);
		}
	}

	public <T extends Annotation> T getTailorAnnotation(Class<T> clazz) {
		if (clazz == null || tailorAnnotations == null) {
			return null;
		}
		//noinspection unchecked
		return (T) tailorAnnotations.get(clazz);
	}
}
