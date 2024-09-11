package org.tomfoolery.infrastructures.repositories;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

import java.util.HashMap;
import java.util.Optional;

public class InMemoryDictionaryEntryRepository implements DictionaryEntryRepository {
    private final HashMap<String, DictionaryEntry> dictionaryEntryHashMap = new HashMap<>();

    @Override
    public Optional<DictionaryEntry> getDictionaryEntry(String word) {
        return Optional.ofNullable(dictionaryEntryHashMap.get(word));
    }

    @Override
    public void addDictionaryEntry(DictionaryEntry dictionaryEntry) {
        dictionaryEntryHashMap.putIfAbsent(dictionaryEntry.headword, dictionaryEntry);
    }
}
