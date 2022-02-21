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

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed4.data.DataObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class YamlObject extends YamlValue<MappingNode> implements DataObject<YamlValue<Node>, YamlList, YamlObject> {
	private final Map<String, NodeTuple> nodes;

	public YamlObject(MappingNode yamlNode) {
		super(yamlNode);
		nodes = new HashMap<>();
		for (NodeTuple nodeTuple : yamlNode.getValue()) {
			nodes.put(((ScalarNode) nodeTuple.getKeyNode()).getValue(), nodeTuple);
		}
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public boolean has(String key) {
		return nodes.containsKey(key);
	}

	@Override
	public void remove(String key) {
		NodeTuple nodeTuple = nodes.get(key);
		if (nodeTuple != null) {
			getNode().getValue().remove(nodeTuple);
		}
		nodes.remove(key);
	}

	@Override
	public YamlValue<Node> set(String key, YamlValue<Node> value) {
		setInternal(key, value);
		return value;
	}

	protected void setInternal(String key, YamlValue<? extends Node> yamlValue) {
		remove(key);
		ScalarNode keyNode = new ScalarNode(Tag.STR, key, StringUtils.isAlphanumeric(key) ? ScalarStyle.PLAIN : ScalarStyle.DOUBLE_QUOTED);
		yamlValue.setKeyNode(keyNode);
		NodeTuple nodeTuple = new NodeTuple(
				keyNode,
				yamlValue.getNode()
		);
		getNode().getValue().add(nodeTuple);
		nodes.put(key, nodeTuple);
	}

	protected YamlValue<Node> create(String key, Tag tag, String value, ScalarStyle scalarStyle) {
		YamlValue<Node> yamlValue = new YamlValue<>(new ScalarNode(tag, value, scalarStyle));
		set(key, yamlValue);
		return yamlValue;
	}

	@Override
	public YamlValue<Node> set(String key, boolean value) {
		return create(key, Tag.BOOL, Boolean.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(String key, String value) {
		return create(key, Tag.STR, value, ScalarStyle.DOUBLE_QUOTED);
	}

	@Override
	public YamlValue<Node> set(String key, char value) {
		return create(key, Tag.STR, Character.toString(value), ScalarStyle.SINGLE_QUOTED);
	}

	@Override
	public YamlValue<Node> set(String key, double value) {
		return create(key, Tag.FLOAT, Double.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(String key, float value) {
		return create(key, Tag.FLOAT, Float.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(String key, long value) {
		return create(key, Tag.INT, Long.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(String key, int value) {
		return create(key, Tag.INT, Integer.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(String key, short value) {
		return create(key, Tag.INT, Short.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(String key, byte value) {
		return create(key, Tag.INT, Byte.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> addNull(String key) {
		return create(key, Tag.NULL, "null", ScalarStyle.PLAIN);
	}

	@Override
	public YamlObject addObject(String key) {
		YamlObject yamlObject = new YamlObject(new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO));
		setInternal(key, yamlObject);
		return yamlObject;
	}

	@Override
	public YamlList addList(String key) {
		YamlList yamlList = new YamlList(new SequenceNode(Tag.SEQ, new ArrayList<>(), FlowStyle.AUTO));
		setInternal(key, yamlList);
		return yamlList;
	}

	@Override
	public YamlValue<Node> get(String key) {
		return new YamlValue<>(nodes.get(key));
	}

	@Override
	public @NotNull Iterator<Pair<String, YamlValue<Node>>> iterator() {
		return nodes.entrySet().stream()
				.map(entry -> Pair.of(entry.getKey(), new YamlValue<>(entry.getValue().getValueNode()))).iterator();
	}
}
