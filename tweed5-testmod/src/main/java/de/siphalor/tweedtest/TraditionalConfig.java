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

package de.siphalor.tweedtest;

import de.siphalor.tweed5.config.ConfigCategory;
import de.siphalor.tweed5.config.ConfigFile;
import de.siphalor.tweed5.config.entry.ValueConfigEntry;
import de.siphalor.tweed5.config.value.serializer.ConfigSerializers;
import de.siphalor.tweed5.data.hjson.HjsonSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TraditionalConfig {
	public static final ConfigCategory ROOT = new ConfigCategory();
	public static final ValueConfigEntry<Integer> AN_INT = ROOT.register("an_int", new ValueConfigEntry<>(12))
			.setComment("an integer");
	public static final ValueConfigEntry<String> NAME = ROOT.register("name", new ValueConfigEntry<>("Hello Config!"))
			.setComment("some random name");
	public static final ValueConfigEntry<List<Float>> RATIOS = ROOT.register("ratios", new ValueConfigEntry<>(
			Arrays.asList(12.12f, 45.45f),
			ConfigSerializers.createList(ConfigSerializers.getFloat(), ArrayList::new)
	));

	public static final ConfigFile FILE = new ConfigFile("tweed_testmod_traditional", HjsonSerializer.INSTANCE, ROOT);
}
