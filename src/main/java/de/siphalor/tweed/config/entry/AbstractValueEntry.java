package de.siphalor.tweed.config.entry;

import com.google.common.collect.Iterators;
import de.siphalor.tweed.config.*;
import de.siphalor.tweed.config.constraints.Constraint;
import de.siphalor.tweed.config.constraints.ConstraintException;
import net.minecraft.util.PacketByteBuf;
import org.hjson.CommentStyle;
import org.hjson.CommentType;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
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
public abstract class AbstractValueEntry<V, T extends AbstractValueEntry> extends AbstractBasicEntry<T> {

	/**
	 * The value of this entry
	 */
	public V value;

	protected V mainConfigValue;
	protected boolean datapackOverridden = false;

	protected V defaultValue;
	protected ArrayDeque<Constraint<V>> preConstraints;
	protected ArrayDeque<Constraint<V>> postConstraints;

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
		this.preConstraints = new ArrayDeque<>();
		this.postConstraints = new ArrayDeque<>();
	}

	public V getDefaultValue() {
		return defaultValue;
	}

	public final boolean isDatapackOverridden() {
		return datapackOverridden;
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
    		preConstraints.push(constraint);
    	else
    		postConstraints.push(constraint);
    	return (T) this;
    }

    @Override
	public final void applyPreConstraints(JsonValue jsonValue) throws ConstraintException {
		for(Constraint<V> constraint : preConstraints) {
			try {
				constraint.apply(jsonValue, this);
			} catch (ConstraintException e) {
				if(e.fatal)
					throw e;
			}
		}
	}

	@Override
    public final void applyPostConstraints(JsonValue jsonValue) throws ConstraintException {
		for(Constraint<V> constraint : postConstraints) {
			try {
				constraint.apply(jsonValue, this);
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
			description.append(getTranslatedComment()).append(System.lineSeparator());
		description.append("default: ").append(writeValue(defaultValue).toString());
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
	 * @param jsonValue The json to read from.
	 * @return The read and converted value;
	 */
	public abstract V readValue(JsonValue jsonValue) throws ConfigReadException;

	@Override
	public final void read(JsonValue json, ConfigEnvironment environment, ConfigScope scope, ConfigOrigin origin) throws ConfigReadException {
		value = readValue(json);
		if(origin == ConfigOrigin.MAIN) {
			mainConfigValue = value;
			datapackOverridden = false;
		} else {
			datapackOverridden = true;
		}
	}

	/**
	 * Abstract method to read in a value and <b>return it</b>. <i>Do not change {@link AbstractValueEntry#value}.</i>
	 * @param buf The buffer to read from.
	 * @return The read and converted value;
	 */
	public abstract V readValue(PacketByteBuf buf);

	@Override
	public final void read(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope) {
		if(environment.contains(getEnvironment())) {
			if(scope.triggers(getScope())) {
				value = readValue(buf);
				onReload();
			} else {
				mainConfigValue = readValue(buf);
			}
		} else {
			readValue(buf);
		}
	}

	/**
	 * Abstract method to convert a specific value of the generic type to a {@link JsonValue}
	 * @param value the value to convert
	 * @return the converted value
	 */
	public abstract JsonValue writeValue(V value);

	@Override
    public final void write(JsonObject jsonObject, String key, ConfigEnvironment environment, ConfigScope scope) {
    	jsonObject.set(key, writeValue(mainConfigValue));
    	jsonObject.setComment(key, CommentType.BOL, CommentStyle.LINE, getDescription());
    }

    public abstract void writeValue(PacketByteBuf buf);

	@Override
	public final void write(PacketByteBuf buf, ConfigEnvironment environment, ConfigScope scope) {
		writeValue(buf);
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
