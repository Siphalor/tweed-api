package de.siphalor.tweed4.config.constraints;

import com.mojang.datafixers.util.Pair;

import java.util.List;

public interface Constraint<T> {
	Result<T> apply(T value);

	String getDescription();

	class Result<T> {
		public final boolean ok;
		public final List<Pair<Severity, String>> messages;
		public final T value;

		public Result(boolean ok, T value, List<Pair<Severity, String>> messages) {
			this.ok = ok;
			this.messages = messages;
			this.value = value;
		}
	}

	enum Severity { INFO, WARN, ERROR }
}
