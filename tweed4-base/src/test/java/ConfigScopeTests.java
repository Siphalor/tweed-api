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

import de.siphalor.tweed4.config.ConfigScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigScopeTests {
	@Test
	public void testTriggers() {
		boolean[] expected = new boolean[] {
				false, false, false, false, false, // UNSPECIFIED
				false, true,  true,  true,  true,
				false, false, true,  true,  true,
				false, false, false, true,  true,
				false, false, false, false, true,
		};
		int i = 0;
		for (ConfigScope scope : ConfigScope.ENUM.values()) {
			for (ConfigScope other : ConfigScope.ENUM.values()) {
				Assertions.assertEquals(expected[i++], scope.triggers(other), scope + " triggers " + other);
			}
		}
	}
}
