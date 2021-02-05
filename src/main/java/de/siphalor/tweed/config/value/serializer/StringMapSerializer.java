package de.siphalor.tweed.config.value.serializer;

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed.Tweed;
import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataObject;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

import java.util.Map;
import java.util.function.Supplier;

public class StringMapSerializer<V, M extends Map<String, V>> extends ConfigValueSerializer<M> {
	ConfigValueSerializer<V> valueSerializer;
	Supplier<M> mapSupplier;

	public StringMapSerializer(ConfigValueSerializer<V> valueSerializer, Supplier<M> mapSupplier) {
		this.valueSerializer = valueSerializer;
		this.mapSupplier = mapSupplier;
	}

	@Override
	public M read(DataValue<?> data) throws ConfigReadException {
		M map = mapSupplier.get();
		if (data.isObject()) {
			DataObject<?> dataObject = data.asObject();
			for (Pair<String, ? extends DataValue<?>> pair : dataObject) {
				try {
					map.put(pair.getFirst(), valueSerializer.read(pair.getSecond()));
				} catch (ConfigReadException e) {
					Tweed.LOGGER.error("Failed to serialize \"" + pair.getSecond() + "\"  using " + valueSerializer.getClass().getName());
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, M value) {
		DataObject<?> dataObject = dataContainer.addObject(key);
		for (Map.Entry<String, V> entry : value.entrySet()) {
			valueSerializer.write(dataObject, entry.getKey(), entry.getValue());
		}
	}

	@Override
	public M read(PacketByteBuf packetByteBuf) {
		M map = mapSupplier.get();
		int size = packetByteBuf.readVarInt();
		for (int i = 0; i < size; i++) {
			map.put(packetByteBuf.readString(32767), valueSerializer.read(packetByteBuf));
		}
		return map;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, M value) {
		packetByteBuf.writeVarInt(value.size());
		for (Map.Entry<String, V> entry : value.entrySet()) {
			packetByteBuf.writeString(entry.getKey());
			valueSerializer.write(packetByteBuf, entry.getValue());
		}
	}

	@Override
	public String asString(M value) {
		StringBuilder builder = new StringBuilder("{\n");
		for (Map.Entry<String, V> entry : value.entrySet()) {
			builder.append("\t");
			builder.append(entry.getKey());
			builder.append(": ");
			builder.append(valueSerializer.asString(entry.getValue()));
			builder.append(",\n");
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public Class<M> getType() {
		//noinspection unchecked
		return (Class<M>) mapSupplier.get().getClass();
	}
}
