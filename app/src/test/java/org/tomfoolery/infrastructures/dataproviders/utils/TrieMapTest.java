package org.tomfoolery.infrastructures.dataproviders.utils;

import lombok.val;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TrieMapTest {
    @Test
    public void test() {
        TrieMap<Integer> trieMap = TrieMap.of();

        trieMap.put("hello", 0);
        trieMap.put("world", 1);
        trieMap.put("work", 2);

        val valueOfHello = trieMap.get("hello");
        assertEquals(valueOfHello, 0);
    }

    @Test
    public void testSize() {
    }

    @Test
    public void testIsEmpty() {
    }

    @Test
    public void testContainsKey() {
    }

    @Test
    public void testContainsKeyWithPrefix() {
    }

    @Test
    public void testContainsValue() {
    }

    @Test
    public void testGet() {
    }

    @Test
    public void testGetByPrefix() {
    }

    @Test
    public void testPut() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testPutAll() {
    }

    @Test
    public void testClear() {
    }

    @Test
    public void testKeySet() {
    }

    @Test
    public void testValues() {
    }

    @Test
    public void testEntrySet() {
    }
}