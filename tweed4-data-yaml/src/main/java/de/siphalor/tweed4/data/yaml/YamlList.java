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
import de.siphalor.tweed4.data.DataSerializer;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.SequenceNode;

import java.util.AbstractList;

public class YamlList extends AbstractList<Node> implements DataList<Node> {
	private final SequenceNode yamlNode;

	public YamlList(SequenceNode yamlNode) {
		this.yamlNode = yamlNode;
	}

	@Override
	public Node getValue() {
		return yamlNode;
	}

	@Override
	public String getComment(int index) {
		return YamlSerializer.getComment(yamlNode.getValue().get(index));
	}

	@Override
	public void setComment(int index, String comment) {
		YamlSerializer.setComment(yamlNode.getValue().get(index), comment);
	}

	@Override
	public DataSerializer<Node> getSerializer() {
		return YamlSerializer.INSTANCE;
	}

	@Override
	public Node get(int index) {
		return yamlNode.getValue().get(index);
	}

	@Override
	public int size() {
		return yamlNode.getValue().size();
	}

	@Override
	public Node set(int index, Node element) {
		return yamlNode.getValue().set(index, element);
	}

	@Override
	public void add(int index, Node element) {
		yamlNode.getValue().add(index, element);
	}

	@Override
	public Node remove(int index) {
		return yamlNode.getValue().remove(index);
	}
}
