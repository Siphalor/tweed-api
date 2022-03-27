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

import de.siphalor.tweed4.config.ConfigEnvironment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigEnvironmentTests {
	@Test
	public void testTriggers() {
		Boolean[] expected = new Boolean[] {
				true,  true,  true,  true,  true, // UNIVERSAL
				true,  true,  false, false, null, // CLIENT
				true,  false, true,  false, null, // SERVER
				false, false, false, true,  null, // SYNCED
				null,  null,  null,  null,  null, // DEFAULT
		};
		int i = 0;
		for (ConfigEnvironment environment : ConfigEnvironment.values()) {
			for (ConfigEnvironment other : ConfigEnvironment.values()) {
				if (expected[i] != null) {
					Assertions.assertEquals(expected[i], environment.triggers(other), environment + " triggers " + other);
				}
				i++;
			}
		}
	}

	@Test
	@Deprecated
	public void testContains() {
		boolean[] expected = new boolean[] {
				true,  true,  true,  false, false, // UNIVERSAL
				false, true,  false, false, false, // CLIENT
				false, false, true,  false, false, // SERVER
				false, false, false, true,  false, // SYNCED
				false, false, false, false, true,  // DEFAULT
		};
		int i = 0;
		for (ConfigEnvironment environment : ConfigEnvironment.values()) {
			for (ConfigEnvironment other : ConfigEnvironment.values()) {
				Assertions.assertEquals(expected[i++], environment.contains(other), environment + " contains " + other);
			}
		}
	}
}
