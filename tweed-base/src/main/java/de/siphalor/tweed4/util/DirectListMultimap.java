package de.siphalor.tweed4.util;

import com.google.common.collect.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DirectListMultimap<K, V, L extends List<V>> implements ListMultimap<K, V> {
	private final Map<K, L> delegate;
	private final Supplier<L> listSupplier;

	public DirectListMultimap(Map<K, L> delegate, Supplier<L> listSupplier) {
		this.delegate = delegate;
		this.listSupplier = listSupplier;
	}

	@Override
	public L get(K key) {
		return delegate.get(key);
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public Multiset<K> keys() {
		return ImmutableMultiset.copyOf(delegate.entrySet().stream().flatMap(entry -> {
			int size = entry.getValue().size();
			//noinspection unchecked
			K[] keys = (K[]) new Object[size];
			Arrays.fill(keys, entry.getKey());
			return Arrays.stream(keys);
		}).collect(Collectors.toSet()));
	}

	@Override
	public Collection<V> values() {
		return delegate.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
	}

	@Override
	public Collection<Map.Entry<K, V>> entries() {
		return delegate.entrySet().stream().flatMap(entry ->
			entry.getValue().stream().map(value -> Maps.immutableEntry(entry.getKey(), value))
		).collect(Collectors.toSet());
	}

	@Override
	public L removeAll(Object key) {
		//noinspection SuspiciousMethodCalls
		L old = delegate.remove(key);
		return old == null ? listSupplier.get() : old;
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public int size() {
		return delegate.values().stream().mapToInt(List::size).sum();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		//noinspection SuspiciousMethodCalls
		return delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		//noinspection SuspiciousMethodCalls
		return delegate.values().stream().anyMatch(list -> list.contains(value));
	}

	@Override
	public boolean containsEntry(Object key, Object value) {
		//noinspection SuspiciousMethodCalls
		return delegate.containsKey(key) &&
				delegate.get(key).contains(value);
	}

	@Override
	public boolean put(K key, V value) {
		if (delegate.containsKey(key)) {
			delegate.get(key).add(value);
		} else {
			L list = listSupplier.get();
			list.add(value);
			delegate.put(key, list);
		}
		return true;
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	@Override
	public boolean remove(Object key, Object value) {
		if (delegate.containsKey(key)) {
			delegate.get(key).remove(value);
			return true;
		}
		return false;
	}

	@Override
	public boolean putAll(K key, Iterable<? extends V> values) {
		if (delegate.containsKey(key)) {
			L list = delegate.get(key);
			values.forEach(list::add);
		} else {
			L list = listSupplier.get();
			values.forEach(list::add);
			delegate.put(key, list);
		}
		return true;
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		multimap.asMap().forEach(this::putAll);
		return true;
	}

	@Override
	public L replaceValues(K key, Iterable<? extends V> values) {
		L old = delegate.remove(key);
		putAll(key, values);
		return old == null ? listSupplier.get() : old;
	}

	@Override
	public Map<K, Collection<V>> asMap() {
		//noinspection unchecked
		return (Map<K, Collection<V>>)(Object) delegate;
	}
}
