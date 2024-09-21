package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.jmolecules.ddd.types.Entity;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.ddd.types.ValueObject;

import java.util.HashMap;
import java.util.List;

/**
 * <code>DictionaryEntry</code> is an entity within the transactional boundaries
 * of aggregate root <code>Dictionary</code>. <br>
 * Adhering to Clean Architecture, <code>DictionaryEntry</code> was originally intended
 * to be managed by a repository; this means that it should be made an DDD's aggregate root.
 * However, that means that it could be referenced and manipulated independently of
 * the <code>Dictionary</code> aggregate; in other words, the aggregate root loses control of
 * its entities.
 *
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
