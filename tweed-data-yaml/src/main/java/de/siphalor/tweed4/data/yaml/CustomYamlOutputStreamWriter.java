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

package de.siphalor.tweed4.data.yaml;

import org.snakeyaml.engine.v2.api.YamlOutputStreamWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class CustomYamlOutputStreamWriter extends YamlOutputStreamWriter {
	public CustomYamlOutputStreamWriter(OutputStream out, Charset cs) {
		super(out, cs);
	}

	@Override
	public void processIOException(IOException e) {

	}
}
