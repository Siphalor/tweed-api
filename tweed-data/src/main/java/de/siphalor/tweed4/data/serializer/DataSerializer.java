package de.siphalor.tweed4.data.serializer;

import de.siphalor.tweed4.data.DataList;

@SuppressWarnings("deprecation")
public interface DataSerializer<RawValue> extends ConfigDataSerializer<RawValue> {
	DataList<RawValue> newList();
}
