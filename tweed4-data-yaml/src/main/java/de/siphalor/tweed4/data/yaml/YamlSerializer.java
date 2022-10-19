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

import de.siphalor.tweed4.data.AnnotatedDataValue;
import de.siphalor.tweed4.data.DataNull;
import de.siphalor.tweed4.data.DataSerializer;
import de.siphalor.tweed4.data.DataType;
import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.events.CommentEvent;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;
import org.snakeyaml.engine.v2.serializer.Serializer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class YamlSerializer implements DataSerializer<Node> {
	public static final YamlSerializer INSTANCE = new YamlSerializer();

	private static final LoadSettings LOAD_SETTINGS = LoadSettings.builder().setParseComments(true).build();
	private static final DumpSettings DUMP_SETTINGS = DumpSettings.builder().setIndent(2).build();

	@Override
	public AnnotatedDataValue<Node> read(InputStream inputStream) {
		return new Composer(LOAD_SETTINGS, new ParserImpl(
				LOAD_SETTINGS,
				new StreamReader(LOAD_SETTINGS, new InputStreamReader(inputStream))
		)).getSingleNode().map(node -> AnnotatedDataValue.of(node, getComment(node))).orElse(null);
	}

	@Override
	public void write(OutputStream outputStream, AnnotatedDataValue<Node> dataValue) {
		Serializer serializer = new Serializer(
				DUMP_SETTINGS,
				new Emitter(DUMP_SETTINGS, new CustomYamlOutputStreamWriter(outputStream, StandardCharsets.UTF_8))
		);
		serializer.open();
		setComment(dataValue.getValue(), dataValue.getComment());
		serializer.serialize(dataValue.getValue());
		serializer.close();
	}

	@Override
	public Object toRaw(Node value, @Nullable DataType typeHint) {
		if (value instanceof ScalarNode) {
			ScalarNode scalarNode = (ScalarNode) value;
			if (scalarNode.getTag().equals(Tag.BOOL)) {
				return Boolean.parseBoolean(scalarNode.getValue());
			} else if (scalarNode.getTag().equals(Tag.INT)) {
				if (typeHint != null) {
					return typeHint.cast(scalarNode.getValue());
				}
				return Integer.parseInt(scalarNode.getValue());
			} else if (scalarNode.getTag().equals(Tag.FLOAT)) {
				if (typeHint != null) {
					return typeHint.cast(Double.parseDouble(scalarNode.getValue()));
				}
				return Double.parseDouble(scalarNode.getValue());
			} else if (scalarNode.getTag().equals(Tag.NULL)) {
				return DataNull.INSTANCE;
			} else {
				return scalarNode.getValue();
			}
		} else {
			return null;
		}
	}

	@Override
	public Node fromRawPrimitive(Object raw) {
		if (raw instanceof Boolean) {
			return new ScalarNode(Tag.BOOL, (Boolean) raw ? "true" : "false", ScalarStyle.PLAIN);
		} else if (raw instanceof Integer) {
			return new ScalarNode(Tag.INT, raw.toString(), ScalarStyle.PLAIN);
		} else if (raw instanceof Double) {
			return new ScalarNode(Tag.FLOAT, raw.toString(), ScalarStyle.PLAIN);
		} else if (raw instanceof String) {
			return new ScalarNode(Tag.STR, (String) raw, DUMP_SETTINGS.getDefaultScalarStyle());
		} else if (raw instanceof DataNull) {
			return new ScalarNode(Tag.NULL, "null", ScalarStyle.PLAIN);
		} else {
			return null;
		}
	}

	@Override
	public YamlObject newObject() {
		return new YamlObject(new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO));
	}

	@Override
	public YamlList newList() {
		return new YamlList(new SequenceNode(Tag.SEQ, new ArrayList<>(), FlowStyle.AUTO));
	}

	@Override
	public String getFileExtension() {
		return "yaml";
	}

	@Override
	public String getId() {
		return "tweed4:yaml";
	}

	public static @Nullable String getComment(Node node) {
		List<CommentLine> blockComments = node.getBlockComments();
		if (blockComments == null || blockComments.isEmpty()) {
			return null;
		}
		return blockComments.stream().map(commentLine -> commentLine.getValue().trim()).collect(Collectors.joining("\n"));
	}

	public static void setComment(Node node, @Nullable String comment) {
		if (comment == null) {
			node.setBlockComments(null);
			return;
		}
		List<CommentLine> commentLines = Arrays.stream(comment.split("\n")).map(line -> new CommentLine(new CommentEvent(CommentType.BLOCK, line, Optional.empty(), Optional.empty()))).collect(Collectors.toList());
		node.setBlockComments(commentLines);
	}
}
