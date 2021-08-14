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

package de.siphalor.tweed4.data.xml;

import de.siphalor.tweed4.data.DataContainer;
import org.w3c.dom.Element;

public abstract class XmlContainer<Key> extends XmlValue implements DataContainer<Key, XmlValue, XmlList, XmlObject> {
	public XmlContainer(Element xmlElement) {
		super(xmlElement);
	}

	protected abstract XmlValue createTypedChild(Key key, String type, String value);

	@Override
	public XmlValue set(Key key, byte value) {
		return createTypedChild(key, "byte", Byte.toString(value));
	}

	@Override
	public XmlValue set(Key key, short value) {
		return createTypedChild(key, "short", Short.toString(value));
	}

	@Override
	public XmlValue set(Key key, int value) {
		return createTypedChild(key, "int", Integer.toString(value));
	}

	@Override
	public XmlValue set(Key key, long value) {
		return createTypedChild(key, "long", Long.toString(value));
	}

	@Override
	public XmlValue set(Key key, float value) {
		return createTypedChild(key, "float", Float.toString(value));
	}

	@Override
	public XmlValue set(Key key, double value) {
		return createTypedChild(key, "double", Double.toString(value));
	}

	@Override
	public XmlValue set(Key key, char value) {
		return createTypedChild(key, "char", Character.toString(value));
	}

	@Override
	public XmlValue set(Key key, String value) {
		return createTypedChild(key, "string", value);
	}

	@Override
	public XmlValue set(Key key, boolean value) {
		return createTypedChild(key, "bool", Boolean.toString(value));
	}

	@Override
	public XmlValue addNull(Key key) {
		return createTypedChild(key, "null", "");
	}
}
