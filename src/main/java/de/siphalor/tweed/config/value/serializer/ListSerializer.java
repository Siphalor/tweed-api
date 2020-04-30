package de.siphalor.tweed.config.value.serializer;

import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataList;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

import java.util.List;
import java.util.function.Supplier;

public class ListSerializer<E, L extends List<E>> extends ConfigValueSerializer<L> {
	ConfigValueSerializer<E> valueSerializer;
	Supplier<L> listSupplier;

	public ListSerializer(ConfigValueSerializer<E> elementSerializer, Supplier<L> listSupplier) {
		this.valueSerializer = elementSerializer;
		this.listSupplier = listSupplier;
	}

	@Override
	public L read(DataValue<?> data) throws ConfigReadException {
		L list = listSupplier.get();
		if (data.isList()) {
			DataList<?> dataList = data.asList();
			for (DataValue<?> dataValue : dataList) {
				list.add(valueSerializer.read(dataValue));
			}
		}
		return list;
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, L value) {
		DataList<?> dataList = dataContainer.addList(key);
		int i = 0;
		for (E element : value) {
			valueSerializer.write(dataList, i, element);
		}
	}

	@Override
	public L read(PacketByteBuf packetByteBuf) {
		int l = packetByteBuf.readVarInt();
		L list = listSupplier.get();
		for (int i = 0; i < l; i++) {
			list.add(valueSerializer.read(packetByteBuf));
		}
		return list;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, L value) {
		packetByteBuf.writeVarInt(value.size());
		for (E element : value) {
			valueSerializer.write(packetByteBuf, element);
		}
	}

	@Override
	public String asString(L value) {
		StringBuilder stringBuilder = new StringBuilder("[ ");
		for (E element : value) {
			stringBuilder.append(valueSerializer.asString(element)).append(", ");
		}
		return stringBuilder.append(" ]").toString();
	}

	@Override
	public Class<L> getType() {
		return (Class<L>) listSupplier.get().getClass();
	}
}
