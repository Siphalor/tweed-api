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

import de.siphalor.tweed4.config.ConfigCategory;
import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.entry.ValueConfigEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigCategoryTests {
	@Test
	public void testGetScope() {
		ConfigCategory category = new ConfigCategory();

		Assertions.assertEquals(ConfigScope.DEFAULT, category.getScope());

		category.setScope(ConfigScope.WORLD);
		Assertions.assertEquals(ConfigScope.WORLD, category.getScope());

		category.register("b", new ValueConfigEntry<>(10).setScope(ConfigScope.SMALLEST));
		Assertions.assertEquals(ConfigScope.WORLD, category.getScope());

		category.register("a", new ValueConfigEntry<>(10).setScope(ConfigScope.GAME));
		Assertions.assertEquals(ConfigScope.GAME, category.getScope());

		category.setScope(ConfigScope.DEFAULT);
		Assertions.assertEquals(ConfigScope.GAME, category.getScope());

		category.setScope(ConfigScope.HIGHEST);
		Assertions.assertEquals(ConfigScope.HIGHEST, category.getScope());
	}

	@Test
	public void testSetScope() {
		ConfigCategory category = new ConfigCategory();
		ValueConfigEntry<Integer> entry = category.register("a", new ValueConfigEntry<>(10));

		Assertions.assertEquals(ConfigScope.DEFAULT, category.getScope());
		Assertions.assertEquals(ConfigScope.DEFAULT, entry.getScope());

		category.setScope(ConfigScope.GAME);
		Assertions.assertEquals(ConfigScope.GAME, category.getScope());
		Assertions.assertEquals(ConfigScope.GAME, entry.getScope());

		ValueConfigEntry<Integer> entry2 = category.register("b", new ValueConfigEntry<>(10));
		Assertions.assertEquals(ConfigScope.GAME, entry2.getScope());
	}

	@Test
	@Deprecated
	public void testGetEnvironment() {
		ConfigCategory category = new ConfigCategory();

		Assertions.assertEquals(ConfigEnvironment.DEFAULT, category.getEnvironment());

		category.setEnvironment(ConfigEnvironment.SERVER);
		Assertions.assertEquals(ConfigEnvironment.SERVER, category.getEnvironment());

		ValueConfigEntry<Integer> entry = category.register("a", new ValueConfigEntry<>(10).setEnvironment(ConfigEnvironment.CLIENT));
		Assertions.assertEquals(ConfigEnvironment.UNIVERSAL, category.getEnvironment());

		entry.setEnvironment(ConfigEnvironment.SERVER);
		Assertions.assertEquals(ConfigEnvironment.SERVER, category.getEnvironment());

		category.setEnvironment(ConfigEnvironment.CLIENT);
		Assertions.assertEquals(ConfigEnvironment.UNIVERSAL, category.getEnvironment());
	}

	@Test
	public void testSetEnvironment() {
		ConfigCategory category = new ConfigCategory();
		ValueConfigEntry<Integer> entry = category.register("a", new ValueConfigEntry<>(10));

		Assertions.assertEquals(ConfigEnvironment.DEFAULT, category.getOwnEnvironment());
		Assertions.assertEquals(ConfigEnvironment.DEFAULT, entry.getOwnEnvironment());

		entry.setEnvironment(ConfigEnvironment.SYNCED);
		category.setEnvironment(ConfigEnvironment.SERVER);
		Assertions.assertEquals(ConfigEnvironment.SERVER, category.getOwnEnvironment());
		Assertions.assertEquals(ConfigEnvironment.SYNCED, entry.getOwnEnvironment());

		ValueConfigEntry<Integer> entry2 = category.register("b", new ValueConfigEntry<>(10));
		Assertions.assertEquals(ConfigEnvironment.SERVER, entry2.getOwnEnvironment());
	}
}
