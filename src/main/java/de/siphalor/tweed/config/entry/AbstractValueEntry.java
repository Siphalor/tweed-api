package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.value.ConfigValue;
import de.siphalor.tweed.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.network.PacketByteBuf;

/**
 * This base class and all derived classes are highly deprecated - Use {@link ValueConfigEntry} and the {@link de.siphalor.tweed.config.value.serializer.ConfigValueSerializer}s instead.
 */
@Deprecated
public abstract class AbstractValueEntry<V, T> extends ValueConfigEntry<V, T> {
	/**
	 * Dummy member for backwards compatibility.
	 */
	public V value;

	@SuppressWarnings("unchecked")
	public AbstractValueEntry(V defaultValue) {
		this(defaultValue, (ConfigValueSerializer<V>) ConfigValue.serializer(defaultValue, defaultValue.getClass()));
	}

	public AbstractValueEntry(V defaultValue, ConfigValueSerializer<V> serializer) {
		super((V) null, serializer);
		this.value = defaultValue;
		this.defaultValue = defaultValue;
		this.currentValue = new DummyValue();
	}

	/**
	 * Abstract method to read in a value and <b>return it</b>. <i>Do not change {@link AbstractValueEntry#currentValue}.</i>
	 * @param dataValue The data to read from.
	 * @return The read and converted value;
	 */
	public abstract V readValue(DataValue<?> dataValue) throws ConfigReadException;

	@Override
	public void read(DataValue<?> dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		currentValue.set(readValue(dataValue));
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = currentValue.get();
		}
		onReload();
	}

	/**
	 * Abstract method to read in a value and <b>return it</b>. <i>Do not change {@link AbstractValueEntry#currentValue}.</i>
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
					currentValue.set(readValue(buf));
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
			writeValue(currentValue.get(), buf);
	}

	private class DummyValue extends ConfigValue<V> {
		@Override
		public V get() {
			return value;
		}

		@Override
		public void set(V v) {
			value = v;
		}
	}
}
