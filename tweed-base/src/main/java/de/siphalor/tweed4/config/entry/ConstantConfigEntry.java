package de.siphalor.tweed4.config.entry;

import de.siphalor.tweed4.config.ConfigEnvironment;
import de.siphalor.tweed4.config.ConfigOrigin;
import de.siphalor.tweed4.config.ConfigReadException;
import de.siphalor.tweed4.config.ConfigScope;
import de.siphalor.tweed4.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed4.data.DataContainer;
import de.siphalor.tweed4.data.DataValue;
import net.minecraft.util.PacketByteBuf;

public class ConstantConfigEntry<V> extends AbstractBasicEntry<V> {
	private final V value;
	private final ConfigValueSerializer<V> valueSerializer;

	public ConstantConfigEntry(V value, ConfigValueSerializer<V> valueSerializer) {
		this.value = value;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {

	}

	@Override
	public void read(DataValue<?> dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {

	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {

	}

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {

	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		valueSerializer.write(dataContainer, key, value);
	}

	@Override
	public String getDescription() {
		return getComment();
	}
}
