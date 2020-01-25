package de.siphalor.tweed.config.entry;

import com.google.common.collect.Iterators;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.constraints.Constraint;
import de.siphalor.tweed.config.constraints.ConstraintException;
import de.siphalor.tweed.data.DataContainer;
import de.siphalor.tweed.data.DataValue;
import net.minecraft.util.PacketByteBuf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

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
@SuppressWarnings("unchecked")
public abstract class AbstractValueEntry<V, T> extends AbstractBasicEntry<T> {

	/**
	 * The value of this entry
	 */
	public V value;

	protected V mainConfigValue;

	protected V defaultValue;
	protected Queue<Constraint<V>> preConstraints;
	protected Queue<Constraint<V>> postConstraints;

	protected Consumer<V> reloadListener;

	/**
	 * Constructs a new entry
	 * @param defaultValue The default value to use
	 */
	public AbstractValueEntry(V defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.mainConfigValue = defaultValue;
		this.comment = "";
		this.environment = ConfigEnvironment.UNIVERSAL;
		this.preConstraints = new ConcurrentLinkedQueue<>();
		this.postConstraints = new ConcurrentLinkedQueue<>();
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
		this.value = value;
		this.mainConfigValue = value;
	}

	@Override
	public void reset(ConfigEnvironment environment, ConfigScope scope) {
		value = defaultValue;
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
		description.append("default: ").append(defaultValue.toString());
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

	/**
	 * Abstract method to read in a value and <b>return it</b>. <i>Do not change {@link AbstractValueEntry#value}.</i>
	 * @param dataValue The data to read from.
	 * @return The read and converted value;
	 */
	public abstract V readValue(DataValue<?> dataValue) throws ConfigReadException;

	@Override
	public final void read(DataValue<?> dataValue, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		value = readValue(dataValue);
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = value;
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
	public final void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(environment.contains(getEnvironment())) {
			if(scope.triggers(getScope())) {
				if(origin == ConfigOrigin.MAIN)
					mainConfigValue = readValue(buf);
				else
					value = readValue(buf);
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
    public final <Key> void write(DataContainer<?, Key> dataContainer, Key key, ConfigEnvironment environment, ConfigScope scope) {
		writeValue(dataContainer, key, mainConfigValue);
        if(dataContainer.has(key)) dataContainer.get(key).setComment(getDescription());
    }

    public abstract void writeValue(V value, PacketByteBuf buf);

	@Override
	public final void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) {
		if(origin == ConfigOrigin.MAIN)
			writeValue(mainConfigValue, buf);
		else
			writeValue(value, buf);
	}

	public T setReloadListener(Consumer<V> listener) {
		this.reloadListener = listener;
		return (T) this;
	}

    public void onReload() {
		if(reloadListener != null)
			reloadListener.accept(value);
	}
}
