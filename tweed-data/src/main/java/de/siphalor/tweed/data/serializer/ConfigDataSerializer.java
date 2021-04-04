package de.siphalor.tweed.data.serializer;

import de.siphalor.tweed.data.DataObject;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConfigDataSerializer<RawValue> {
	DataObject<RawValue> newObject();
	DataObject<RawValue> read(InputStream inputStream);
	void write(OutputStream outputStream, DataObject<RawValue> dataObject);
	String getFileExtension();

	/**
	 * Should return the id of this serializer.
	 * Used when adding serializers through the <code>tweed:serializer</code> entry point.
	 * @return The id of the serializer as <code>modid:name</code>
	 */
	default String getId() {
		return null;
	}
}
