package org.tomfoolery.infrastructures.dataproviders.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
public class Trie<T> implements Map<CharSequence, T> {
    private Node<T> rootNode = Node.of();
    private int size = 0;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    public boolean containsKeyWithPrefix(@NonNull CharSequence prefix) {
        return !this.getByPrefix(prefix).isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        // Highly inaccurate!
        return false;
    }

    @Override
    public T get(Object key) {
        return key instanceof CharSequence
            ? this.get((CharSequence) key)
            : null;
    }

    private T get(@NonNull CharSequence key) {
        var currNode = this.rootNode;

        for (var index = 0; index < key.length(); ++index) {
            val chr = key.charAt(index);

            if (!currNode.successors.containsKey(chr))
                return null;

            currNode = currNode.successors.get(chr);
        }

        return currNode.value;
    }

    public Trie<T> getByPrefix(@NonNull CharSequence prefix) {
        var currNode = this.rootNode;

        for (var index = 0; index < prefix.length(); ++index) {
            val chr = prefix.charAt(index);

            if (!currNode.successors.containsKey(chr))
                return Trie.of();

            currNode = currNode.successors.get(chr);
        }

        // Highly inaccurate!
        return Trie.of(currNode, this.size);
    }

    @Override
    public T put(@NonNull CharSequence key, @NonNull T value) {
        var currNode = this.rootNode;

        for (var index = 0; index < key.length(); ++index) {
            val chr = key.charAt(index);

            if (!currNode.successors.containsKey(chr))
                currNode.successors.put(chr, Node.of());

            currNode = currNode.successors.get(chr);
        }

        val prevValue = currNode.value;
        currNode.value = value;

        ++this.size;

        return prevValue;
    }

    @Override
    public T remove(Object key) {
        return key instanceof CharSequence
            ? this.remove((CharSequence) key)
            : null;
    }

    private T remove(@NonNull CharSequence key) {
        var currNode = this.rootNode;

        for (var index = 0; index < key.length(); ++index) {
            val chr = key.charAt(index);

            if (!currNode.successors.containsKey(chr))
                return null;

            currNode = currNode.successors.get(chr);
        }

        if (currNode.value == null)
            return null;

        val prevValue = currNode.value;
        currNode.value = null;

        --size;

        return prevValue;
    }

    @Override
    public void putAll(@NonNull Map<? extends CharSequence, ? extends T> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        this.rootNode = Node.of();
        this.size = 0;
    }

    @Override
    public @NonNull Set<CharSequence> keySet() {
        return this.entrySet()
            .stream()
            .map(Entry::getKey)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NonNull Set<T> values() {
        return this.entrySet()
            .stream()
            .map(Entry::getValue)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NonNull Set<Entry<CharSequence, T>> entrySet() {
        return entrySet(this.rootNode, "");
    }

    private static <T> @NonNull Set<Entry<CharSequence, T>> entrySet(@NonNull Node<T> node, @NonNull CharSequence prefix) {
        Set<Entry<CharSequence, T>> entries = new HashSet<>();

        if (node.value != null)
            entries.add(new AbstractMap.SimpleEntry<>(prefix, node.value));

        node.successors.forEach((chr, successor) -> {
            val successorEntries = entrySet(successor, prefix.toString() + chr);
            entries.addAll(successorEntries);
        });

        return entries;
    }

    @NoArgsConstructor(staticName = "of")
    @AllArgsConstructor(staticName = "of")
    private static class Node<T> {
        @NonNull public final HashMap<Character, Node<T>> successors = new HashMap<>();
        public T value;
    }
}
