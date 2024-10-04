package org.tomfoolery.infrastructures.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(staticName = "of")
public class Trie<T> implements Map<String, T> {

    private TrieNode root = new TrieNode();
    private int size = 0;

    {
        root = new TrieNode();
        size = 0;
    }

    private class TrieNode {
        Map<Character, TrieNode> children;
        T value;
        boolean isEndOfWord;

        TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }

    @Override
    public int size() {
        return size;   
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private TrieNode getNode(String key) {
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return null;
            }
            node = node.children.get(c);
        }
        return node;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) {
            return false;
        }
        TrieNode node = getNode((String) key);
        return node != null && node.isEndOfWord;
    }

    @Override
    public T get(Object key) {
        if (key == null) {
            return null;
        }
        if (!(key instanceof String)) {
            return null;
        }
        TrieNode node = getNode((String) key);
        return (node != null && node.isEndOfWord) ? node.value : null;
    }

    @Override
    public T put(@NonNull String key, T value) {
        
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        T oldValue = node.value;
        node.value = value;
        if (!node.isEndOfWord) {
            node.isEndOfWord = true;
            size++;
        }
        return oldValue;
    }

    @Override
    public T remove(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        String stringKey = (String) key;
        List<TrieNode> nodes = new ArrayList<>(stringKey.length() + 1);
        TrieNode node = root;
        for (char c : stringKey.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return null;
            }
            nodes.add(node);
            node = node.children.get(c);
        }
        nodes.add(node);

        if (!node.isEndOfWord) {
            return null;
        }

        T oldValue = node.value;
        node.isEndOfWord = false;
        node.value = null;
        size--;

        for (int i = nodes.size() - 1; i > 0; i--) {
            TrieNode current = nodes.get(i);
            TrieNode parent = nodes.get(i - 1);
            if (current.children.isEmpty() && !current.isEndOfWord) {
                parent.children.remove(stringKey.charAt(i - 1));
            } else {
                break;
            }
        }

        return oldValue;
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        for (Entry<? extends String, ? extends T> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        root = new TrieNode();
        size = 0;
    }

    private void collectKeys(TrieNode node, String prefix, Set<String> keys) {
        if (node.isEndOfWord) {
            keys.add(prefix);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectKeys(entry.getValue(), prefix + entry.getKey(), keys);
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        collectKeys(root, "", keys);
        return keys;
    }

    private void collectValues(TrieNode node, List<T> values) {
        if (node.isEndOfWord) {
            values.add(node.value);
        }
        for (TrieNode child : node.children.values()) {
            collectValues(child, values);
        }
    }
    
    @Override
    public Collection<T> values() {
        List<T> values = new ArrayList<>();
        collectValues(root, values);
        return values;
    }

    private void collectEntries(TrieNode node, StringBuilder prefix, Set<Entry<String, T>> entries) {
        if (node.isEndOfWord) {
            entries.add(new AbstractMap.SimpleEntry<>(prefix.toString(), node.value));
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            prefix.append(entry.getKey());
            collectEntries(entry.getValue(), prefix, entries);
            prefix.setLength(prefix.length() - 1);
        }
    }

    @Override
    public Set<Entry<String, T>> entrySet() {
        Set<Entry<String, T>> entries = new HashSet<>();
        collectEntries(root, new StringBuilder(), entries);
        return entries;
    }


    public List<String> prefixSearch(String prefix) {
        List<String> results = new ArrayList<>();
        
        TrieNode node = getNode(prefix);
        
        if (node == null) {
            return results; 
        }
        
        collectWordsWithPrefix(node, new StringBuilder(prefix), results);
        
        return results;
    }

    private void collectWordsWithPrefix(TrieNode node, StringBuilder currentPrefix, List<String> results) {

        if (node.isEndOfWord) {
            results.add(currentPrefix.toString());
        }
        
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            currentPrefix.append(entry.getKey()); 
            collectWordsWithPrefix(entry.getValue(), currentPrefix, results);
            currentPrefix.setLength(currentPrefix.length() - 1); // RETURN
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValueHelper(root, value);
    }

    private boolean containsValueHelper(TrieNode node, Object value) {
        if (node.isEndOfWord && Objects.equals(node.value, value)) {
            return true;
        }
        
        for (TrieNode child : node.children.values()) {
            if (containsValueHelper(child, value)) {
                return true;
            }
        }
        
        return false;
    }
}
