package org.tomfoolery.infrastructures.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class Trie<T> implements Map<String, T> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public T get(Object key) {
        return null;
    }

    @Override
    public T put(String key, T value) {
        return null;
    }

    @Override
    public T remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return Set.of();
    }

    @Override
    public Collection<T> values() {
        return List.of();
    }

    @Override
    public Set<Entry<String, T>> entrySet() {
        return Set.of();
    }
}
