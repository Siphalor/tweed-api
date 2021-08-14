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

package de.siphalor.tweed4.data.yaml;

import de.siphalor.tweed4.data.DataValue;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.events.CommentEvent;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class YamlValue<T extends Node> implements DataValue<YamlValue<Node>, YamlList, YamlObject> {
	private final T yamlNode;
	private Node keyNode;
	private String tempComment;

	public YamlValue(T yamlNode) {
		this(yamlNode, null);
	}

	public YamlValue(NodeTuple tuple) {
		//noinspection unchecked
		this((T) tuple.getValueNode(), tuple.getKeyNode());
	}

	public YamlValue(T yamlNode, Node keyNode) {
		this.yamlNode = yamlNode;
		this.keyNode = keyNode;
	}

	private boolean isBetween(long value, int min, int max) {
		return value >= min && value <= max;
	}

	private ScalarNode getScalar() {
		if (yamlNode instanceof ScalarNode) {
			return ((ScalarNode) yamlNode);
		}
		throw new RuntimeException("Tried to interpret non-scalar YAML node as scalar!");
	}

	protected T getNode() {
		return yamlNode;
	}

	public void setKeyNode(Node keyNode) {
		if (this.keyNode != null) {
			keyNode.setBlockComments(this.keyNode.getBlockComments());
			this.keyNode = keyNode;
		} else {
			this.keyNode = keyNode;
			setComment(tempComment);
		}
	}

	@Override
	public void setComment(String comment) {
		if (keyNode != null) {
			if (comment == null) {
				keyNode.setBlockComments(null);
			} else {
				keyNode.setBlockComments(
						Arrays.stream(comment.split("\n")).map(line ->
								new CommentLine(
										new CommentEvent(CommentType.BLOCK, " " + line, Optional.empty(), Optional.empty())
								)
						).collect(Collectors.toList())
				);
			}
		} else {
			tempComment = comment;
		}
	}

	@Override
	public String getComment() {
		if (keyNode != null) {
			List<CommentLine> comments = keyNode.getBlockComments();
			if (comments.isEmpty()) return null;
			return comments.stream().map(commentLine -> commentLine.getValue().trim()).collect(Collectors.joining("\n"));
		}

		return tempComment;
	}

	@Override
	public boolean isNumber() {
		return yamlNode.getTag() == Tag.INT || yamlNode.getTag() == Tag.FLOAT;
	}

	@Override
	public boolean isByte() {
		return yamlNode.getTag() == Tag.INT && isBetween(asLong(), Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	@Override
	public boolean isShort() {
		return yamlNode.getTag() == Tag.INT && isBetween(asLong(), Short.MIN_VALUE, Short.MAX_VALUE);
	}

	@Override
	public boolean isInt() {
		return yamlNode.getTag() == Tag.INT && isBetween(asLong(), Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public boolean isLong() {
		return yamlNode.getTag() == Tag.INT;
	}

	@Override
	public boolean isFloat() {
		return yamlNode.getTag() == Tag.FLOAT;
	}

	@Override
	public boolean isDouble() {
		return yamlNode.getTag() == Tag.FLOAT;
	}

	@Override
	public boolean isChar() {
		return yamlNode.getTag() == Tag.STR && asString().length() == 1;
	}

	@Override
	public boolean isString() {
		return yamlNode.getTag() == Tag.STR;
	}

	@Override
	public boolean isBoolean() {
		return yamlNode.getTag() == Tag.BOOL;
	}

	@Override
	public boolean isObject() {
		return yamlNode.getTag() == Tag.MAP;
	}

	@Override
	public boolean isList() {
		return yamlNode.getTag() == Tag.SEQ;
	}

	@Override
	public boolean isNull() {
		return yamlNode.getTag() == Tag.NULL;
	}

	@Override
	public Number asNumber() {
		return asDouble();
	}

	@Override
	public byte asByte() {
		return Byte.parseByte(getScalar().getValue());
	}

	@Override
	public short asShort() {
		return Short.parseShort(getScalar().getValue());
	}

	@Override
	public int asInt() {
		return Integer.parseInt(getScalar().getValue());
	}

	@Override
	public long asLong() {
		return Long.parseLong(getScalar().getValue());
	}

	@Override
	public float asFloat() {
		return Float.parseFloat(getScalar().getValue());
	}

	@Override
	public double asDouble() {
		return Double.parseDouble(getScalar().getValue());
	}

	@Override
	public char asChar() {
		return getScalar().getValue().charAt(0);
	}

	@Override
	public String asString() {
		return getScalar().getValue();
	}

	@Override
	public boolean asBoolean() {
		return Boolean.parseBoolean(getScalar().getValue());
	}

	@Override
	public YamlObject asObject() {
		YamlObject yamlObject = new YamlObject(((MappingNode) getNode()));
		yamlObject.setKeyNode(keyNode);
		return yamlObject;
	}

	@Override
	public YamlList asList() {
		YamlList yamlList = new YamlList((SequenceNode) getNode());
		yamlList.setKeyNode(keyNode);
		return yamlList;
	}

	@Override
	public Object getRaw() {
		return null;
	}
}
