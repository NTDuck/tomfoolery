package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.HashMap;
import java.util.List;

@Data(staticConstructor = "of")
public class DictionaryEntry {
    private final @NonNull ID id;
    private final @NonNull @Singular List<Meaning> meanings;

    private List<String> syllables;
    private HashMap<String, String> pronunciations;   // Maps partOfSpeech to pronunciation label
    private Number frequencyScore;

    @Value(staticConstructor = "of")
    public static class ID {
        @NonNull String headword;
    }

    @Value(staticConstructor = "of")
    public static class Meaning {
        @NonNull String definition;
        @NonNull String partOfSpeech;

        @NonNull @Singular HashMap<String, List<String>> properties;
    }
}
