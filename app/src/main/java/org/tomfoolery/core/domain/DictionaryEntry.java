package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.Value;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Entity;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.ddd.types.ValueObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @see <a href="https://dictionaryapi.dev/">Free Dictionary API</a>
 * @see <a href="https://dictionaryapi.com/products/json#sec-2">Merriam-Webster's Collegiate Dictionary API</a>
 */
@Data(staticConstructor = "of")
public class DictionaryEntry implements Entity<Dictionary, DictionaryEntry.ID> {
    @NonNull private final ID id;
    @NonNull @Singular private List<Meaning> meanings;

    private List<String> syllables;
    private HashMap<String, String> pronunciations;   // Maps partOfSpeech to pronunciation label
    private Number frequencyScore;

    public String getHeadword() {
        return this.id.getHeadword();
    }

    @Value(staticConstructor = "of")
    public static class ID implements Identifier {
        @NonNull String headword;
    }

    @Value(staticConstructor = "of")
    public static class Meaning implements ValueObject {
        @NonNull String definition;
        @NonNull String partOfSpeech;

        @NonNull @Singular HashMap<String, List<String>> properties;
    }
}
