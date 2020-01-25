package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.*;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

/**
 * An entry to register at a {@link de.siphalor.tweed.config.ConfigFile} or {@link ConfigCategory}.
 * @param <V> the type which is used for maintaining the value of the entry. Use {@link AbstractValueEntry#value} to access
 * @param <T> the derived class (to support chain calls)
 * @see BooleanEntry
 * @see FloatEntry
 * @see IntEntry
 * @see MappedEnumEntry
 * @see StringEntry
 */
@Deprecated
public abstract class AbstractValueEntry<V, T> extends ValueEntry<V, T> {
	public AbstractValueEntry(V defaultValue) {
		super(defaultValue);
	}

	/**
	 * Abstract method to read in a value and <b>return it</b>. <i>Do not change {@link AbstractValueEntry#value}.</i>
	 * @param dataValue The data to read from.
	 * @return The read and converted value;
	 */
	public abstract V readValue(DataValue<?> dataValue) throws ConfigReadException;

	@Override
	public void read(DataValue<?> dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		value.set(readValue(dataValue));
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = value.get();
		}
		onReload();
	}

	/**
	 * Abstract method to read in a value and <b>return it</b>. <i>Do not change {@link AbstractValueEntry#value}.</i>
	 * @param buf The buffer to read from.
	 * @return The read and converted value;
	 */
	public abstract V readValue(PacketByteBuf buf);

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(environment.contains(getEnvironment())) {
			if(scope.triggers(getScope())) {
				if(origin == ConfigOrigin.MAIN)
					mainConfigValue = readValue(buf);
				else
					value.set(readValue(buf));
				onReload();
			}
			else if(origin == ConfigOrigin.MAIN)
				mainConfigValue = readValue(buf);
		} else {
			readValue(buf);
		}
	}

	/**
	 * Abstract method to add the generic value to the given data structure
	 *  @param parent the data object to target
	 * @param name the id/key where to store the converted data;
	 * @param value the value to convert
	 */
	public abstract <Key> void writeValue(DataContainer<?, Key> parent, Key name, V value);

	@Override
    public <Key> void write(DataContainer<?, Key> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		writeValue(dataContainer, key, mainConfigValue);
        if(dataContainer.has(key)) dataContainer.get(key).setComment(getDescription());
    }

    public abstract void writeValue(V value, PacketByteBuf buf);

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(origin == ConfigOrigin.MAIN)
			writeValue(mainConfigValue, buf);
		else
			writeValue(value.get(), buf);
	}
}
