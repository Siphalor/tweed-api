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

import de.siphalor.tweed4.data.DataList;
import org.jetbrains.annotations.NotNull;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class YamlList extends YamlValue<SequenceNode> implements DataList<YamlValue<Node>, YamlList, YamlObject> {
	public YamlList(SequenceNode yamlNode) {
		super(yamlNode);
	}

	@Override
	public int size() {
		return getNode().getValue().size();
	}

	@Override
	public void remove(Integer index) {
		getNode().getValue().remove((int) index);
	}

	@Override
	public YamlValue<Node> get(Integer index) {
		return new YamlValue<>(getNode().getValue().get(index));
	}

	protected void set(int index, Node node) {
		List<Node> list = getNode().getValue();
		for (int i = list.size(); i <= index; i++) {
			list.add(null);
		}
		list.set(index, node);
	}

	protected YamlValue<Node> set(int index, Tag tag, String value, ScalarStyle scalarStyle) {
		ScalarNode node = new ScalarNode(tag, value, scalarStyle);
		set(index, node);
		getNode().getValue().set(index, node);
		return new YamlValue<>(node);
	}

	@Override
	public YamlValue<Node> set(Integer index, byte value) {
		return set(index, Tag.INT, Byte.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(Integer index, short value) {
		return set(index, Tag.INT, Short.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(Integer index, int value) {
		return set(index, Tag.INT, Integer.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(Integer index, long value) {
		return set(index, Tag.INT, Long.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(Integer index, float value) {
		return set(index, Tag.FLOAT, Float.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(Integer index, double value) {
		return set(index, Tag.FLOAT, Double.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(Integer index, char value) {
		return set(index, Tag.STR, Character.toString(value), ScalarStyle.SINGLE_QUOTED);
	}

	@Override
	public YamlValue<Node> set(Integer index, String value) {
		return set(index, Tag.STR, value, ScalarStyle.DOUBLE_QUOTED);
	}

	@Override
	public YamlValue<Node> set(Integer index, boolean value) {
		return set(index, Tag.BOOL, Boolean.toString(value), ScalarStyle.PLAIN);
	}

	@Override
	public YamlValue<Node> set(Integer index, YamlValue<Node> value) {
		set(index, value.getNode());
		return value;
	}

	@Override
	public YamlValue<Node> addNull(Integer index) {
		return set(index, Tag.NULL, "null", ScalarStyle.PLAIN);
	}

	@Override
	public YamlList addList(Integer index) {
		SequenceNode node = new SequenceNode(Tag.SEQ, new ArrayList<>(), FlowStyle.AUTO);
		set(index, node);
		return new YamlList(node);
	}

	@Override
	public YamlObject addObject(Integer index) {
		MappingNode node = new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO);
		set(index, node);
		return new YamlObject(node);
	}

	@NotNull
	@Override
	public Iterator<YamlValue<Node>> iterator() {
		return getNode().getValue().stream().map(YamlValue::new).iterator();
	}
}
