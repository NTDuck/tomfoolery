package org.tomfoolery.core.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://dictionaryapi.dev/">Free Dictionary API</a>
 * @see <a href="https://dictionaryapi.com/products/json#sec-2">Merriam-Webster's Collegiate Dictionary API</a>
 */
public class DictionaryEntry {
    public String headword;

    public static class Meaning {
        public String definition;
        public String partOfSpeech;

        public HashMap<String, List<String>> properties;
    }

    public List<Meaning> meanings;

    public List<String> syllables;
    public HashMap<String, String> pronunciations;   // Maps partOfSpeech to pronunciation label
    public Number frequencyScore;
}
