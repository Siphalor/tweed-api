package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.annotated.AConfigConstraint;
import de.siphalor.tweed.config.constraints.Constraint;
import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.config.value.ConfigValue;
import de.siphalor.tweed.config.value.SimpleConfigValue;
import de.siphalor.tweed.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import de.siphalor.tweed.tailor.ClothTailor;
import net.minecraft.network.PacketByteBuf;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An entry to register at a {@link ConfigFile} or {@link ConfigCategory}.
 * @param <V> the type which is used for maintaining the value of the entry. Use {@link ValueConfigEntry#currentValue} to access
 */
@SuppressWarnings("unchecked")
public class ValueConfigEntry<V> extends AbstractBasicEntry<ValueConfigEntry<V>> {
	private final ConfigValueSerializer<V> valueSerializer;
	@SuppressWarnings("rawtypes")
	private final ClothTailor.EntryConverter customConverter;

	/**
	 * The value of this entry. Will be renamed when backwards compatibility is dropped.
	 */
	ConfigValue<V> currentValue;

	protected V mainConfigValue;

	protected V defaultValue;
	protected Queue<Constraint<V>> constraints;

	protected Consumer<V> reloadListener;

	/**
	 * Constructs a new entry
	 * @param defaultValue The default value to use
	 */
	public ValueConfigEntry(V defaultValue) {
		this(new SimpleConfigValue<>(defaultValue), (ConfigValueSerializer<V>) ConfigValue.serializer(defaultValue, defaultValue.getClass()), null);
	}

	public ValueConfigEntry(V defaultValue, ConfigValueSerializer<V> configValueSerializer) {
		this(new SimpleConfigValue<>(defaultValue), configValueSerializer, null);
	}

	public ValueConfigEntry(ConfigValue<V> configValue, ConfigValueSerializer<V> valueSerializer) {
		this(configValue, valueSerializer, null);
	}

	@SuppressWarnings("rawtypes")
	public ValueConfigEntry(ConfigValue<V> configValue, ConfigValueSerializer<V> valueSerializer, ClothTailor.EntryConverter customConverter) {
		this.valueSerializer = valueSerializer;
		this.customConverter = customConverter;
		this.currentValue = configValue;
		this.defaultValue = currentValue.get();
		this.mainConfigValue = defaultValue;
		this.comment = "";
		this.environment = ConfigEnvironment.UNIVERSAL;
		this.constraints = new ConcurrentLinkedQueue<>();
	}

	public V getValue() {
		return currentValue.get();
	}

	public void setValue(V value) {
		this.currentValue.set(value);
	}

	/**
	 * Sets the default value. Use with care!
	 * @param defaultValue the new default value ("new default" lol)
	 */
	public void setDefaultValue(V defaultValue) {
		this.defaultValue = defaultValue;
	}

	public V getDefaultValue() {
		return defaultValue;
	}

	public void setMainConfigValue(V mainConfigValue) {
		this.mainConfigValue = mainConfigValue;
	}

	public final V getMainConfigValue() {
		return mainConfigValue;
	}

	@Deprecated
	public void setBothValues(V value) {
		this.currentValue.set(value);
		this.mainConfigValue = value;
	}

	public Class<V> getType() {
		return valueSerializer.getType();
	}

	/**
	 * Returns a custom {@link ClothTailor.EntryConverter} that was set using an {@link AConfigConstraint} annotation
	 * @return the {@link ClothTailor.EntryConverter}
	 */
	@SuppressWarnings("rawtypes")
	public ClothTailor.EntryConverter getCustomEntryConverter() {
		return customConverter;
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
		currentValue.set(defaultValue);
		mainConfigValue = defaultValue;
	}

	/**
	 * Register a constraint
	 * @param constraint the new constraint
	 * @return this entry for chain calls
	 */
	public final ValueConfigEntry<V> addConstraint(Constraint<V> constraint) {
		constraints.add(constraint);
		return this;
	}

	public Queue<Constraint<V>> getConstraints() {
		return constraints;
	}

	@Override
	public final void applyConstraints() throws ConstraintException {
		applyConstraints(getValue());
	}

	public final void applyConstraints(V value) throws ConstraintException {
		for(Constraint<V> constraint : constraints) {
			try {
				constraint.apply(value, this);
			} catch (ConstraintException e) {
				if(e.fatal)
					throw e;
			}
		}
	}

	@Override
	public String getDescription() {
		StringBuilder description = new StringBuilder();
		if(comment.length() > 0)
			description.append(getComment()).append(System.lineSeparator());
		description.append("default: ").append(valueSerializer != null ? valueSerializer.asString(defaultValue) : defaultValue.toString());

		String constraintDesc = constraints.stream().flatMap(constraint -> {
			String desc = constraint.getDescription();
			if (desc.isEmpty())
				return Stream.empty();
			return Arrays.stream(desc.split("\n"));
		}).collect(Collectors.joining(System.lineSeparator() + "\t"));
		if (!constraintDesc.isEmpty()) {
			description.append('\n').append(constraintDesc);
		}

		return description.toString();
	}

	@Override
	public void read(DataValue<?> dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		currentValue.set(valueSerializer.read(dataValue));
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = currentValue.get();
		}
		onReload();
	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(environment.triggers(getEnvironment())) {
			if(scope.triggers(getScope())) {
				if(origin == ConfigOrigin.MAIN)
					mainConfigValue = valueSerializer.read(buf);
				else
					currentValue.set(valueSerializer.read(buf));
				onReload();
			}
			else if(origin == ConfigOrigin.MAIN)
				mainConfigValue = valueSerializer.read(buf);
		} else {
			valueSerializer.read(buf);
		}
	}

	@Override
	public <Key> void write(DataContainer<?, Key> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		valueSerializer.write(dataContainer, key, mainConfigValue);
		if(dataContainer.has(key)) dataContainer.get(key).setComment(getDescription());
	}

	@Override
	public void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(origin == ConfigOrigin.MAIN)
			valueSerializer.write(buf, mainConfigValue);
		else
			valueSerializer.write(buf, currentValue.get());
	}

	public ValueConfigEntry<V> setReloadListener(Consumer<V> listener) {
		this.reloadListener = listener;
		return this;
	}

	public void onReload() {
		if(reloadListener != null)
			reloadListener.accept(currentValue.get());
	}
}
