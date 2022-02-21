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

import de.siphalor.tweed4.data.DataSerializer;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;
import org.snakeyaml.engine.v2.serializer.Serializer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class YamlSerializer implements DataSerializer<YamlValue<Node>, YamlList, YamlObject> {
	public static final YamlSerializer INSTANCE = new YamlSerializer();

	private static final LoadSettings LOAD_SETTINGS = LoadSettings.builder().setParseComments(true).build();
	private static final DumpSettings DUMP_SETTINGS = DumpSettings.builder().setIndent(2).build();

	@Override
	public YamlValue<Node> readValue(InputStream inputStream) {
		Optional<Node> rootNode = new Composer(LOAD_SETTINGS, new ParserImpl(
				LOAD_SETTINGS,
				new StreamReader(LOAD_SETTINGS, new InputStreamReader(inputStream))
		)).getSingleNode();
		return rootNode.map(YamlValue::new).orElse(null);
	}

	@Override
	public void writeValue(OutputStream outputStream, YamlValue<Node> dataValue) {
		Serializer serializer = new Serializer(
				DUMP_SETTINGS,
				new Emitter(DUMP_SETTINGS, new CustomYamlOutputStreamWriter(outputStream, StandardCharsets.UTF_8))
		);
		serializer.open();
		serializer.serialize(dataValue.getNode());
		serializer.close();
	}

	@Override
	public YamlList newList() {
		return new YamlList(new SequenceNode(Tag.SEQ, new ArrayList<>(), FlowStyle.AUTO));
	}

	@Override
	public YamlValue<Node> newBoolean(boolean value) {
		return new YamlValue<>(new ScalarNode(Tag.BOOL, Boolean.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newChar(char value) {
		return new YamlValue<>(new ScalarNode(Tag.STR, Character.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newString(String value) {
		return new YamlValue<>(new ScalarNode(Tag.STR, value, ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newByte(byte value) {
		return new YamlValue<>(new ScalarNode(Tag.INT, Byte.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newShort(short value) {
		return new YamlValue<>(new ScalarNode(Tag.INT, Short.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newInt(int value) {
		return new YamlValue<>(new ScalarNode(Tag.INT, Integer.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newLong(long value) {
		return new YamlValue<>(new ScalarNode(Tag.INT, Long.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newFloat(float value) {
		return new YamlValue<>(new ScalarNode(Tag.FLOAT, Float.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlValue<Node> newDouble(double value) {
		return new YamlValue<>(new ScalarNode(Tag.FLOAT, Double.toString(value), ScalarStyle.PLAIN));
	}

	@Override
	public YamlObject newObject() {
		return new YamlObject(new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO));
	}

	@Override
	public YamlValue<Node> newNull() {
		return new YamlValue<>(new ScalarNode(Tag.NULL, "null", ScalarStyle.PLAIN));
	}

	@Override
	public String getFileExtension() {
		return "yaml";
	}

	@Override
	public String getId() {
		return "tweed4:yaml";
	}
}
