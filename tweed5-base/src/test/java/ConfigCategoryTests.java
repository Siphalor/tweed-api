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

import de.siphalor.tweed5.config.ConfigCategory;
import de.siphalor.tweed5.reload.ReloadEnvironment;
import de.siphalor.tweed5.reload.ReloadScope;
import de.siphalor.tweed5.config.entry.ValueConfigEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigCategoryTests {
	@Test
	public void testGetScope() {
		ConfigCategory category = new ConfigCategory();

		Assertions.assertEquals(ReloadScope.UNSPECIFIED, category.getScope());

		category.setScope(ReloadScope.WORLD);
		Assertions.assertEquals(ReloadScope.WORLD, category.getScope());

		category.register("b", new ValueConfigEntry<>(10).setScope(ReloadScope.SMALLEST));
		Assertions.assertEquals(ReloadScope.WORLD, category.getScope());

		category.register("a", new ValueConfigEntry<>(10).setScope(ReloadScope.GAME));
		Assertions.assertEquals(ReloadScope.GAME, category.getScope());

		category.setScope(ReloadScope.UNSPECIFIED);
		Assertions.assertEquals(ReloadScope.GAME, category.getScope());

		category.setScope(ReloadScope.HIGHEST);
		Assertions.assertEquals(ReloadScope.HIGHEST, category.getScope());
	}

	@Test
	public void testSetScope() {
		ConfigCategory category = new ConfigCategory();
		ValueConfigEntry<Integer> entry = category.register("a", new ValueConfigEntry<>(10));

		Assertions.assertEquals(ReloadScope.UNSPECIFIED, category.getScope());
		Assertions.assertEquals(ReloadScope.UNSPECIFIED, entry.getScope());

		category.setScope(ReloadScope.GAME);
		Assertions.assertEquals(ReloadScope.GAME, category.getScope());
		Assertions.assertEquals(ReloadScope.GAME, entry.getScope());

		ValueConfigEntry<Integer> entry2 = category.register("b", new ValueConfigEntry<>(10));
		Assertions.assertEquals(ReloadScope.GAME, entry2.getScope());
	}

	@Test
	public void testSetEnvironment() {
		ConfigCategory category = new ConfigCategory();
		ValueConfigEntry<Integer> entry = category.register("a", new ValueConfigEntry<>(10));

		Assertions.assertEquals(ReloadEnvironment.UNSPECIFIED, category.getOwnEnvironment());
		Assertions.assertEquals(ReloadEnvironment.UNSPECIFIED, entry.getOwnEnvironment());

		entry.setEnvironment(ReloadEnvironment.SYNCED);
		category.setEnvironment(ReloadEnvironment.SERVER);
		Assertions.assertEquals(ReloadEnvironment.SERVER, category.getOwnEnvironment());
		Assertions.assertEquals(ReloadEnvironment.SYNCED, entry.getOwnEnvironment());

		ValueConfigEntry<Integer> entry2 = category.register("b", new ValueConfigEntry<>(10));
		Assertions.assertEquals(ReloadEnvironment.SERVER, entry2.getOwnEnvironment());
	}
}
