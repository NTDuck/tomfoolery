package org.tomfoolery.infrastructures.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TrieTest {
    private final Trie<String> trie = Trie.of();

    @Test
    public void testPutAndGet() {
        trie.put("key1", "value1");
        String value = trie.get("key1");
        Assert.assertEquals(value, "value1", "The value should match the inserted value.");
    }

    @Test
    public void testSizeAfterAddingEntries() {
        trie.put("key1", "value1");
        trie.put("key2", "value2");
        Assert.assertEquals(trie.size(), 2, "Map size should be 2 after adding 2 entries.");
    }

    @Test
    public void testContainsKey() {
        trie.put("key1", "value1");
        Assert.assertTrue(trie.containsKey("key1"), "The map should contain the key 'key1'.");
        Assert.assertFalse(trie.containsKey("key2"), "The map should not contain the key 'key2'.");
    }

   @Test
   public void testContainsValue() {
       trie.put("key1", "value1");
       Assert.assertTrue(trie.containsValue("value1"), "The map should contain the value 'value1'.");
       Assert.assertFalse(trie.containsValue("value2"), "The map should not contain the value 'value2'.");
   }

    @Test
    public void testRemove() {
        trie.put("key1", "value1");
        trie.remove("key1");
        Assert.assertFalse(trie.containsKey("key1"), "The map should not contain the key 'key1' after removal.");
    }

   @Test
   public void testIsEmpty() {
       trie.clear(); 
       Assert.assertTrue(trie.isEmpty(), "The map should be empty initially.");
       trie.put("key1", "value1");
       Assert.assertFalse(trie.isEmpty(), "The map should not be empty after adding an entry.");
   }

    @Test
    public void testOverwriteValue() {
        trie.put("key1", "value1");
        trie.put("key1", "newValue1");
        Assert.assertEquals(trie.get("key1"), "newValue1", "The value should be overwritten with 'newValue1'.");
    }
}