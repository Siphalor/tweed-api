package de.siphalor.tweed.config.entry;

import com.google.common.collect.Iterators;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.constraints.Constraint;
import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.config.value.ConfigValue;
import de.siphalor.tweed.config.value.SimpleConfigValue;
import de.siphalor.tweed.config.value.serializer.ConfigValueSerializer;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * An entry to register at a {@link ConfigFile} or {@link ConfigCategory}.
 * @param <V> the type which is used for maintaining the value of the entry. Use {@link ValueEntry#value} to access
 * @param <T> the derived class (to support chain calls)
 */
@SuppressWarnings("unchecked")
public class ValueEntry<V, T> extends AbstractBasicEntry<T> {
	private ConfigValueSerializer<V> valueSerializer;

	/**
	 * The value of this entry
	 */
	protected ConfigValue<V> value;

	protected V mainConfigValue;

	protected V defaultValue;
	protected Queue<Constraint<V>> preConstraints;
	protected Queue<Constraint<V>> postConstraints;

	protected Consumer<V> reloadListener;

	/**
	 * Constructs a new entry
	 * @param defaultValue The default value to use
	 */
	public ValueEntry(V defaultValue) {
		this(new SimpleConfigValue<>(defaultValue), (ConfigValueSerializer<V>) ConfigValue.serializer(defaultValue));
	}

	public ValueEntry(V defaultValue, ConfigValueSerializer<V> configValueSerializer) {
		this(new SimpleConfigValue<>(defaultValue), configValueSerializer);
	}

	public ValueEntry(ConfigValue<V> configValue, ConfigValueSerializer<V> valueSerializer) {
		this.valueSerializer = valueSerializer;
		this.value = configValue;
		this.defaultValue = value.get();
		this.mainConfigValue = defaultValue;
		this.comment = "";
		this.environment = ConfigEnvironment.UNIVERSAL;
		this.preConstraints = new ConcurrentLinkedQueue<>();
		this.postConstraints = new ConcurrentLinkedQueue<>();
	}

	public V getValue() {
		return value.get();
	}

	public void setValue(V value) {
		this.value.set(value);
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

	public final V getMainConfigValue() {
		return mainConfigValue;
	}

	public void setMainConfigValue(V mainConfigValue) {
		this.mainConfigValue = mainConfigValue;
	}

	public void setBothValues(V value) {
		this.value.set(value);
		this.mainConfigValue = value;
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
		value.set(defaultValue);
		mainConfigValue = defaultValue;
	}

	/**
	 * Register a constraint
	 * @param constraint the new constraint
	 * @return this entry for chain calls
	 */
	public final T addConstraint(Constraint<V> constraint) {
    	if(constraint.getConstraintType() == Constraint.Type.PRE)
    		preConstraints.add(constraint);
    	else
    		postConstraints.add(constraint);
    	return (T) this;
    }

	public Queue<Constraint<V>> getPreConstraints() {
		return preConstraints;
	}

	public Queue<Constraint<V>> getPostConstraints() {
		return postConstraints;
	}

	@Override
	public final void applyPreConstraints(DataValue<?> dataValue) throws ConstraintException {
		for(Constraint<V> constraint : preConstraints) {
			try {
				constraint.apply(dataValue, this);
			} catch (ConstraintException e) {
				if(e.fatal)
					throw e;
			}
		}
	}

	@Override
    public final void applyPostConstraints(DataValue<?> dataValue) throws ConstraintException {
		for(Constraint<V> constraint : postConstraints) {
			try {
				constraint.apply(dataValue, this);
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
		ArrayList<String> constraintDescriptions = new ArrayList<>();
		for(Iterator<Constraint<V>> it = Iterators.concat(preConstraints.iterator(), postConstraints.iterator()); it.hasNext(); ) {
			Constraint<V> constraint = it.next();
			String desc = constraint.getDescription();
			if(desc.isEmpty())
				continue;
			constraintDescriptions.add(desc.replace(System.lineSeparator(), System.lineSeparator() + "\t"));
		}
		if(constraintDescriptions.size() > 0) {
			description.append(System.lineSeparator()).append("constraints:");
			for(String desc : constraintDescriptions) {
				description.append(System.lineSeparator()).append(desc);
			}
		}
		return description.toString();
	}

	@Override
	public void read(DataValue<?> dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		value.set(valueSerializer.read(dataValue));
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = value.get();
		}
		onReload();
	}

	@Override
	public void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(environment.contains(getEnvironment())) {
			if(scope.triggers(getScope())) {
				if(origin == ConfigOrigin.MAIN)
					mainConfigValue = valueSerializer.read(buf);
				else
					value.set(valueSerializer.read(buf));
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
			valueSerializer.write(buf, value.get());
	}

	public T setReloadListener(Consumer<V> listener) {
		this.reloadListener = listener;
		return (T) this;
	}

    public void onReload() {
		if(reloadListener != null)
			reloadListener.accept(value.get());
	}
}
