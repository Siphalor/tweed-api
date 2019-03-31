package de.siphalor.tweed.config.entry;

import de.siphalor.tweed.config.ConfigDefinitionScope;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.constraints.Constraint;
import de.siphalor.tweed.config.constraints.ConstraintException;
import org.hjson.CommentStyle;
import org.hjson.CommentType;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.concurrent.SynchronousQueue;

public abstract class ConfigEntry<T> {

	public T value;

	protected T defaultValue;
	protected String comment;
	protected ConfigEnvironment environment;
	protected ConfigDefinitionScope definitionScope;
	protected String categoryPath;
	protected SynchronousQueue<Constraint<T>> constraints;

	public ConfigEntry(T defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.comment = "";
		this.environment = ConfigEnvironment.UNIVERSAL;
		this.definitionScope = ConfigDefinitionScope.NONE;
		this.categoryPath = "";
		this.constraints = new SynchronousQueue<>();
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

	public abstract void read(JsonValue json);
    public abstract JsonValue write(T value);

    public final void registerConstraint(Constraint<T> constraint) {
    	constraints.add(constraint);
    }

    public final void applyConstraints() throws ConstraintException {
		for(Constraint<T> constraint : constraints) {
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
    	String wholeComment = "";
    	if(comment.length() > 0)
            wholeComment += comment + System.lineSeparator();
    	wholeComment += "\tdefault: " + write(defaultValue).toString();
    	jsonObject.setComment(key, CommentType.BOL, CommentStyle.LINE, wholeComment);
    }

}
