package de.siphalor.tweed.config.constraints;

public interface AnnotationConstraint<T> extends Constraint<T> {
	void fromAnnotationParam(String param, Class<?> valueType);
}
