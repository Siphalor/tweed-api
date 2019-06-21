package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigOrigin;
import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataList;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListEntry<T extends ConfigEntry> extends AbstractBasicEntry<ListEntry<T>> {
	protected Supplier<T> entrySupplier;

	protected List<T> valueList;
	protected List<T> mainValueList;
	protected final List<T> defaultList;

	public ListEntry(List<T> defaultList, Supplier<T> entrySupplier) {
		this.defaultList = defaultList;
		this.entrySupplier = entrySupplier;
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
		valueList = defaultList;
	}

	@Override
	public void read(DataValue dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		if(dataValue.isList()) {
			DataList dataList = dataValue.asList();
			valueList = new ArrayList<>(dataList.size());

			for(int i = 0; i < dataList.size(); i++) {
				T entry = entrySupplier.get();
				try {
					entry.read(dataList.get(i), environment, scope, origin);
					valueList.add(entry);
				} catch (ConfigReadException e) {
					e.printStackTrace();
				}
			}
		} else
			throw new ConfigReadException(dataValue.asString() + " is not an array");
	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		List<T> list;
		list = new ArrayList<>();
		if(origin == ConfigOrigin.MAIN)
			mainValueList = list;
		else
			valueList = list;
        while(buf.readBoolean()) {
            T entry = entrySupplier.get();
            entry.read(buf, environment, scope, origin);
            list.add(entry);
		}
	}

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
        for(T entry : (origin == ConfigOrigin.MAIN ? mainValueList : valueList)) {
        	buf.writeBoolean(true);
        	entry.write(buf, environment, scope, origin);
		}
        buf.writeBoolean(false);
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		DataList dataList = dataContainer.addList(key);
		for(int i = 0; i < mainValueList.size(); i++) {
			mainValueList.get(i).write(dataList, i, environment, scope);
		}
		dataContainer.set(key, dataList);
	}

	@Override
	public String getDescription() {
		return getComment();
	}
}
