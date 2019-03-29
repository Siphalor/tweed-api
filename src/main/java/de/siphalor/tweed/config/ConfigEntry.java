package de.siphalor.tweed.config;

import org.hjson.CommentStyle;
import org.hjson.CommentType;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

public abstract class ConfigEntry<T> {

	public T value;

	protected T defaultValue;
	protected String comment;
	protected ConfigEnvironment environment;
	protected ConfigDefinitionScope definitionScope;
	protected String categoryPath;

	public ConfigEntry(T defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.comment = "";
		this.environment = ConfigEnvironment.UNIVERSAL;
		this.definitionScope = ConfigDefinitionScope.NONE;
		this.categoryPath = "";
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

	public final void reset() {
		value = defaultValue;
	}

	public abstract void read(JsonValue json);
    public abstract JsonValue write(T value);

    public final void write(JsonObject jsonObject, String key) {
    	jsonObject.set(key, write(value));
    	String wholeComment = "";
    	if(comment.length() > 0)
            wholeComment += comment + System.lineSeparator();
    	wholeComment += "\tdefault: " + write(defaultValue).toString();
    	jsonObject.setComment(key, CommentType.BOL, CommentStyle.LINE, wholeComment);
    }

}
