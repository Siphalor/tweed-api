package de.siphalor.tweed.config.entry;

import com.google.common.collect.Iterators;
import de.siphalor.tweed.config.ConfigDefinitionScope;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.constraints.Constraint;
import de.siphalor.tweed.config.constraints.ConstraintException;
import org.hjson.CommentStyle;
import org.hjson.CommentType;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ConfigEntry<T> {

	public T value;

	protected T defaultValue;
	protected String comment;
	protected ConfigEnvironment environment;
	protected ConfigDefinitionScope definitionScope;
	protected String categoryPath;
	protected ArrayDeque<Constraint<T>> preConstraints;
	protected ArrayDeque<Constraint<T>> postConstraints;

	public ConfigEntry(T defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.comment = "";
		this.environment = ConfigEnvironment.UNIVERSAL;
		this.definitionScope = ConfigDefinitionScope.NONE;
		this.categoryPath = "";
		this.preConstraints = new ArrayDeque<>();
		this.postConstraints = new ArrayDeque<>();
	}

	public ConfigEntry setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public ConfigEntry setEnvironment(ConfigEnvironment range) {
		this.environment = range;
		return this;
	}

	public ConfigEnvironment getEnvironment() {
		return environment;
	}

	public ConfigEntry setDefinitionScope(ConfigDefinitionScope definitionScope) {
		this.definitionScope = definitionScope;
		return this;
	}

	public ConfigDefinitionScope getDefinitionScope() {
		return definitionScope;
	}

	public ConfigEntry setCategoryPath(String categoryPath) {
		this.categoryPath = categoryPath;
		return this;
	}

	public String getCategoryPath() {
		return categoryPath;
	}

	public final void reset() {
		value = defaultValue;
	}

	/**
	 * Abstract method for reading the entry's value from a {@link JsonValue} object
	 * @param json the given json value
	 */
	public abstract void read(JsonValue json);
	/**
	 * Abstract method to convert a specific value of the generic type to a {@link JsonValue}
	 * @param value the value to convert
	 * @return the converted value
	 */
    public abstract JsonValue write(T value);

	/**
	 * Register a constraint
	 * @param constraint the new constraint
	 * @return this entry for chain calls
	 */
	public final ConfigEntry registerConstraint(Constraint<T> constraint) {
    	if(constraint.getConstraintType() == Constraint.Type.PRE)
    		preConstraints.push(constraint);
    	else
    		postConstraints.push(constraint);
    	return this;
    }

	public final void applyPreConstraints() throws ConstraintException {
		for(Constraint<T> constraint : preConstraints) {
			try {
				constraint.apply(this);
			} catch (ConstraintException e) {
				if(e.fatal)
					throw e;
			}
		}
	}

    public final void applyPostConstraints() throws ConstraintException {
		for(Constraint<T> constraint : postConstraints) {
			try {
				constraint.apply(this);
			} catch (ConstraintException e) {
				if(e.fatal)
					throw e;
			}
		}
    }

    public final void write(JsonObject jsonObject, String key) {
    	jsonObject.set(key, write(value));
    	StringBuilder wholeComment = new StringBuilder();
    	if(comment.length() > 0)
            wholeComment.append(comment).append(System.lineSeparator());
    	wholeComment.append("default: ").append(write(defaultValue).toString());
	    ArrayList<String> constraintDescriptions = new ArrayList<>();
		for(Iterator<Constraint<T>> it = Iterators.concat(preConstraints.iterator(), postConstraints.iterator()); it.hasNext(); ) {
			Constraint constraint = it.next();
			String desc = constraint.getDescription();
			if(desc.isEmpty())
				continue;
			constraintDescriptions.add(desc);
		}
	    if(constraintDescriptions.size() > 0) {
	    	wholeComment.append(System.lineSeparator()).append("constraints:");
	    	for(String desc : constraintDescriptions) {
	    		wholeComment.append(System.lineSeparator()).append(desc);
		    }
	    }
    	jsonObject.setComment(key, CommentType.BOL, CommentStyle.LINE, wholeComment.toString());
    }

}
