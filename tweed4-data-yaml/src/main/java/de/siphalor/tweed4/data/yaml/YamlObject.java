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

import de.siphalor.tweed4.data.CollectionUtils;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataSerializer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class YamlObject implements DataObject<Node> {
	private final MappingNode yamlNode;
	private final Map<String, NodeTuple> nodes;

	public YamlObject(MappingNode yamlNode) {
		this.yamlNode = yamlNode;
		nodes = new HashMap<>();
		for (NodeTuple nodeTuple : yamlNode.getValue()) {
			nodes.put(((ScalarNode) nodeTuple.getKeyNode()).getValue(), nodeTuple);
		}
	}

	@Override
	public @NotNull Node getValue() {
		return yamlNode;
	}

	@Override
	public String getComment(String key) {
		return YamlSerializer.getComment(nodes.get(key).getKeyNode());
	}

	@Override
	public void setComment(String key, String comment) {
		YamlSerializer.setComment(nodes.get(key).getKeyNode(), comment);
	}

	@Override
	public boolean has(String key) {
		return nodes.containsKey(key);
	}

	@Override
	public DataSerializer<Node> getSerializer() {
		return YamlSerializer.INSTANCE;
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return nodes.containsValue(value);
	}

	@Override
	public Node get(Object key) {
		return nodes.get(key).getValueNode();
	}

	@Nullable
	@Override
	public Node put(String key, Node value) {
		remove(key);
		ScalarNode keyNode = new ScalarNode(Tag.STR, key, StringUtils.isAlphanumeric(key) ? ScalarStyle.PLAIN : ScalarStyle.DOUBLE_QUOTED);
		NodeTuple nodeTuple = new NodeTuple(keyNode, value);
		yamlNode.getValue().add(nodeTuple);
		NodeTuple old = nodes.put(key, nodeTuple);
		if (old == null) {
			return null;
		}
		return old.getValueNode();
	}

	@Override
	public Node remove(Object key) {
		NodeTuple nodeTuple = nodes.remove(key);
		if (nodeTuple == null) {
			return null;
		}
		yamlNode.getValue().remove(nodeTuple);
		return nodeTuple.getValueNode();
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends Node> m) {
		for (Map.Entry<? extends String, ? extends Node> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		nodes.clear();
		yamlNode.getValue().clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return nodes.keySet();
	}

	@NotNull
	@Override
	public Collection<Node> values() {
		return CollectionUtils.mapCollection(nodes.values(), NodeTuple::getValueNode);
	}

	@NotNull
	@Override
	public Set<Entry<String, Node>> entrySet() {
		return CollectionUtils.mapSet(nodes.entrySet(), entry -> new Entry<String, Node>() {
			@Override
			public String getKey() {
				return entry.getKey();
			}

			@Override
			public Node getValue() {
				return entry.getValue().getValueNode();
			}

			@Override
			public Node setValue(Node value) {
				NodeTuple tuple = entry.getValue();
				Node old = tuple.getValueNode();
				NodeTuple newTuple = new NodeTuple(tuple.getKeyNode(), value);
				entry.setValue(newTuple);
				return old;
			}
		});
	}
}
