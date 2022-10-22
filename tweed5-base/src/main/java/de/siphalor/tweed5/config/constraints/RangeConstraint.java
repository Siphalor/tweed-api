/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.tweed5.config.constraints;

import com.mojang.datafixers.util.Pair;
import de.siphalor.tweed5.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class RangeConstraint<T extends Number> implements AnnotationConstraint<T> {
	private boolean autoCorrect;
	protected T min;
	protected T max;

	public RangeConstraint() {
		this(false);
	}

	public RangeConstraint(boolean autoCorrect) {
		super();
		this.autoCorrect = autoCorrect;
	}

	public RangeConstraint<T> between(T min, T max) {
		this.min = min;
		this.max = max;
		return this;
	}

	public RangeConstraint<T> greaterThan(T min) {
		this.min = min;
		this.max = null;
		return this;
	}

	public RangeConstraint<T> smallerThan(T max) {
		this.min = null;
		this.max = max;
		return this;
	}

	public RangeConstraint<T> everything() {
		this.min = null;
		this.max = null;
		return this;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	public boolean hasRealBounds() {
		return min != null && max != null;
	}

	@Override
	public Result<T> apply(T value) {
        if(min != null && value.doubleValue() < min.doubleValue()) {
        	if (autoCorrect) {
				return new Result<>(true, min, Collections.singletonList(
						Pair.of(Severity.WARN, "Value " + value + " capped at lower bound " + min)
				));
			}
        	return new Result<>(false, null, Collections.singletonList(
        			Pair.of(Severity.ERROR, value + " is less than lower bound " + min)
			));
		}
        if(max != null && value.doubleValue() > max.doubleValue()) {
	        if (autoCorrect) {
	        	return new Result<>(true, max, Collections.singletonList(
	        			Pair.of(Severity.WARN, "Value " + value + " capped at upper bound " + max)
				));
			}
	        return new Result<>(false, null, Collections.singletonList(
	        		Pair.of(Severity.ERROR, value + " exceeds upper bound of " + max)
			));
		}
        return new Result<>(true, value, Collections.emptyList());
	}

	@Override
	public String getDescription() {
		if (min == null) {
			if (max == null) {
				return "Any numerical value";
			} else {
				return "Must be less or equal to " + max;
			}
		} else {
			if (max == null) {
				return "Must be greater or equal to " + min;
			} else {
				return "Must be inclusively between " + min + " and " + max;
			}
		}
	}

	public T clampValue(T value) {
		if(value.doubleValue() > min.doubleValue())
			return value.doubleValue() > max.doubleValue() ? max : value;
		else
			return min;
	}

	@SuppressWarnings({"RedundantCast", "unchecked"})
	@Override
	public void fromAnnotationParam(String param, Class<?> valueType) {
		if (param.endsWith("!")) {
			autoCorrect = true;
			param = param.substring(0, param.length() - 1);
		}
		String[] parts = StringUtils.splitByWholeSeparator(param, "..", 2);
		if (parts.length == 0) {
			throw new RuntimeException("Invalid value \"" + param + "\" for number range constraint");
		}
		if (parts[0].isEmpty()) {
			if (parts.length < 2 || parts[1].isEmpty()) {
				everything();
			} else {
				smallerThan((T)(Object) NumberUtil.parse(parts[1], (Class<? extends Number>) valueType));
			}
		} else {
			if (parts.length < 2 || parts[1].isEmpty()) {
				greaterThan((T)(Object) NumberUtil.parse(parts[0], (Class<? extends Number>) valueType));
			} else {
				between(
						(T)(Object) NumberUtil.parse(parts[0], (Class<? extends Number>) valueType),
						(T)(Object) NumberUtil.parse(parts[1], (Class<? extends Number>) valueType)
				);
			}
		}
	}
}
