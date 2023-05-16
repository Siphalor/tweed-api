/*
 * Copyright 2021-2022 Siphalor
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

package de.siphalor.tweed4.tailor.langjsondescriptions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.siphalor.tweed4.Tweed;
import de.siphalor.tweed4.TweedInitializer;
import de.siphalor.tweed4.config.ConfigCategory;
import de.siphalor.tweed4.config.ConfigFile;
import de.siphalor.tweed4.config.TweedRegistry;
import de.siphalor.tweed4.config.entry.ConfigEntry;
import de.siphalor.tweed4.tailor.Tailor;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class LangJsonDescriptionsTailor extends Tailor implements TweedInitializer {
	private static final String TRANSLATION_PREFIX = "tweed4_tailor_screen.screen.";
	private static final Gson GSON = new Gson();
	private final Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public void tweedRegister() {
		Registry.register(TweedRegistry.TAILORS, new Identifier(Tweed.MOD_ID, "lang_json_descriptions"), this);
	}

	@Override
	public void process(ConfigFile configFile) {
		String modid = configFile.getName();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/" + modid + "/lang/en_us.json");
		if (inputStream == null) {
			logger.error("Failed to load lang file for mod " + modid + " config descriptions may not be available");
			return;
		}

		try (Reader reader = new InputStreamReader(inputStream)) {
			JsonObject langJson = GSON.fromJson(reader, JsonObject.class);

			process(langJson, configFile.getRootCategory(), TRANSLATION_PREFIX + modid);
		} catch (IOException e) {
			logger.error("Failed to load lang file for mod " + modid + " config descriptions may not be available", e);
		}
	}

	private void process(JsonObject langJson, ConfigCategory category, String path) {
		category.entryStream().forEach(entry -> {
			String categoryPath = path + "." + entry.getKey();
			String descriptionKey = categoryPath + ".description";
			ConfigEntry<?> configEntry = entry.getValue();
			if (langJson.has(descriptionKey)) {
				configEntry.setComment(langJson.get(descriptionKey).getAsString());
			}
			if (configEntry instanceof ConfigCategory) {
				process(langJson, (ConfigCategory) configEntry, categoryPath);
			}
		});
	}
}
