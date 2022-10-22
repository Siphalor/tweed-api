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

package de.siphalor.tweed5.data;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionUtils {
	public static <From, To> Iterator<To> mapIterator(Iterator<From> fromIterator, Function<From, To> mapper) {
		return new Iterator<To>() {
			@Override
			public boolean hasNext() {
				return fromIterator.hasNext();
			}

			@Override
			public To next() {
				return mapper.apply(fromIterator.next());
			}

			@Override
			public void remove() {
				fromIterator.remove();
			}
		};
	}

	public static <From, To> Iterator<To> mapIterator(Iterator<From> fromIterator, Function<From, To> mapper, Consumer<From> remover) {
		return new Iterator<To>() {
			private From last;

			@Override
			public boolean hasNext() {
				return fromIterator.hasNext();
			}

			@Override
			public To next() {
				return mapper.apply(last = fromIterator.next());
			}

			@Override
			public void remove() {
				remover.accept(last);
				fromIterator.remove();
			}
		};
	}


	public static <From, To> Set<To> mapSet(Set<From> fromSet, Function<From, To> mapper) {
		return mapSet(fromSet, mapper, keys -> {});
	}

	public static <From, To> Set<To> mapSet(Set<From> fromSet, Function<From, To> mapper, Consumer<Collection<From>> remover) {
		Map<To, List<From>> reverseMap = createReverseMap(fromSet, mapper);

		Predicate<To> actualRemover = (to) -> {
			List<From> fromList = reverseMap.get(to);
			if (fromList == null || fromList.isEmpty())
				return false;
			remover.accept(fromList);
			for (From from : fromList) {
				fromSet.remove(from);
			}
			return true;
		};

		return new AbstractSet<To>() {
			@Override
			public Iterator<To> iterator() {
				return mapIterator(reverseMap.keySet().iterator(), (key) -> key, actualRemover::test);
			}

			@Override
			public int size() {
				return fromSet.size();
			}

			@Override
			public boolean remove(Object o) {
				//noinspection unchecked
				return actualRemover.test((To) o);
			}
		};
	}

	public static <From, To> Collection<To> mapCollection(Collection<From> fromCollection, Function<From, To> mapper) {
		return mapCollection(fromCollection, mapper, keys -> {});
	}

	public static <From, To> Collection<To> mapCollection(Collection<From> fromCollection, Function<From, To> mapper, Consumer<Collection<From>> remover) {
		Map<To, List<From>> reverseMap = createReverseMap(fromCollection, mapper);

		return new AbstractCollection<To>() {
			@Override
			public Iterator<To> iterator() {
				return mapIterator(fromCollection.iterator(), mapper, remove -> {
					List<From> fromList = reverseMap.get(mapper.apply(remove));
					if (fromList == null || fromList.isEmpty())
						return;
					remover.accept(fromList);
				});
			}

			@Override
			public int size() {
				return fromCollection.size();
			}

			@Override
			public boolean remove(Object o) {
				//noinspection SuspiciousMethodCalls
				List<From> fromList = reverseMap.get(o);
				if (fromList == null || fromList.isEmpty())
					return false;
				remover.accept(fromList);
				return fromCollection.removeAll(fromList);
			}
		};
	}

	public static <From, To> Map<To, List<From>> createReverseMap(Collection<From> collection, Function<From, To> mapper) {
		Map<To, List<From>> reverseMap = new HashMap<>();
		for (From from : collection) {
			To to = mapper.apply(from);
			reverseMap.computeIfAbsent(to, (key) -> new ArrayList<>()).add(from);
		}
		return reverseMap;
	}
}
