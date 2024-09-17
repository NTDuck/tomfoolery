package org.tomfoolery.core.domain;

import lombok.*;

import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://dictionaryapi.dev/">Free Dictionary API</a>
 * @see <a href="https://dictionaryapi.com/products/json#sec-2">Merriam-Webster's Collegiate Dictionary API</a>
 */
@Data
@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PROTECTED)
public class DictionaryEntry {
    @NonNull String headword;
    @NonNull @Singular List<Meaning> meanings;

    List<String> syllables;
    HashMap<String, String> pronunciations;   // Maps partOfSpeech to pronunciation label
    Number frequencyScore;

    @Data
    @RequiredArgsConstructor(staticName = "of")
    public static class Meaning {
        @NonNull String definition;
        String partOfSpeech;

        @NonNull @Singular HashMap<String, List<String>> properties;
    }
}
