package de.siphalor.tweed.data.serializer;

import de.siphalor.tweed.data.DataObject;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConfigDataSerializer<RawValue> {
	DataObject<RawValue> newObject();
	DataObject<RawValue> read(InputStream inputStream);
	void write(OutputStream outputStream, DataObject<RawValue> dataObject);
	String getFileExtension();
}
