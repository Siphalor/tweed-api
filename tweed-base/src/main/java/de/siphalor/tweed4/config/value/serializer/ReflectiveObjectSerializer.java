package de.siphalor.tweed4.config.value.serializer;

import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataList;
import de.siphalor.tweed4.data.DataObject;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ReflectiveObjectSerializer<T> extends ConfigValueSerializer<T> {
	private final Class<T> clazz;
	private final Supplier<T> supplier;
	private final Map<String, Entry> entries;

	protected ReflectiveObjectSerializer(Class<T> clazz, Supplier<T> supplier, Map<String, Entry> entries) {
		this.clazz = clazz;
		this.supplier = supplier;
		this.entries = entries;
	}

	@Override
	public <V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	T read(V data) throws ConfigReadException {
		if (data.isObject()) {
			O dataObject = data.asObject();
			T object = supplier.get();

			for (Map.Entry<String, Entry> entry : entries.entrySet()) {
				if (Modifier.isFinal(entry.getValue().field.getModifiers())) {
					continue;
				}
				try {
					entry.getValue().field.set(object, entry.getValue().serializer.read(dataObject.get(entry.getKey())));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return object;
		}
		return null;
	}

	@Override
	public <Key, V extends DataValue<V, L, O>, L extends DataList<V, L, O>, O extends DataObject<V, L, O>>
	void write(DataContainer<Key, V, L, O> dataContainer, Key key, T value) {
		O dataObject = dataContainer.addObject(key);
		for (Map.Entry<String, Entry> entry : entries.entrySet()) {
			try {
				entry.getValue().serializer.write(dataObject, entry.getKey(), entry.getValue().field.get(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public T read(PacketByteBuf packetByteBuf) {
		T object = supplier.get();

		int size = packetByteBuf.readVarInt();
		for (int i = 0; i < size; i++) {
			Entry entry = entries.get(packetByteBuf.readString(32767));
			try {
				entry.field.set(object, entry.serializer.read(packetByteBuf));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, T value) {
		packetByteBuf.writeVarInt(entries.size());
		for (Map.Entry<String, Entry> entry : entries.entrySet()) {
			if (Modifier.isFinal(entry.getValue().field.getModifiers())) {
				continue;
			}
			packetByteBuf.writeString(entry.getKey());
			try {
				entry.getValue().serializer.write(packetByteBuf, entry.getValue().field.get(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String asString(T value) {
		StringBuilder stringBuilder = new StringBuilder(value.getClass().getSimpleName() + " {\n");
		for (Map.Entry<String, Entry> entry : entries.entrySet()) {
			stringBuilder.append(entry.getKey());
			stringBuilder.append(": ");
			try {
				stringBuilder.append(
						Arrays.stream(StringUtils.split(
								entry.getValue().serializer.asString(entry.getValue().field.get(value)), "\n"
						)).map(line -> "\t" + line).collect(Collectors.joining("\n")).trim()
				);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				stringBuilder.append("<I am error>");
			}
			stringBuilder.append(",\n");
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	@Override
	public Class<T> getType() {
		return clazz;
	}

	protected static class Entry {
		protected final Field field;
		protected final ConfigValueSerializer<Object> serializer;

		public Entry(Field field, ConfigValueSerializer<Object> serializer) {
			this.field = field;
			this.serializer = serializer;
		}
	}
}
