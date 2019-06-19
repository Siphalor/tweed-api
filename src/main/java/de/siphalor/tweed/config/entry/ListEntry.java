package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigOrigin;
import de.siphalor.tweed.config.ConfigReadException;
import de.siphalor.tweed.config.ConfigScope;
import net.minecraft.util.PacketByteBuf;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

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
	public void read(JsonValue json, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		if(json.isArray()) {
			JsonArray jsonArray = json.asArray();
			valueList = new ArrayList<>(jsonArray.size());

			for(int i = 0; i < jsonArray.size(); i++) {
				T entry = entrySupplier.get();
				try {
					entry.read(jsonArray.get(i), environment, scope, origin);
					valueList.add(entry);
				} catch (ConfigReadException e) {
					e.printStackTrace();
				}
			}
		} else
			throw new ConfigReadException(json.asString() + " is not an array");
	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		List<T> list;
		list = new ArrayList();
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
	public void write(JsonObject jsonObject, String key, ConfigEnvironment environment, ConfigScope scope) {
		JsonArray jsonArray = new JsonArray();
		for(T entry : mainValueList) {
			JsonObject tempObject = new JsonObject();
            entry.write(tempObject, "_", environment, scope);
            jsonArray.add(tempObject.get("_"));
		}
	}

	@Override
	public String getDescription() {
		return getComment();
	}
}
