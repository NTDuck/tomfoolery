package org.tomfoolery.infrastructures.repositories;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class InMemoryDictionaryEntryRepository implements DictionaryEntryRepository {
    private final HashMap<String, DictionaryEntry> dictionaryEntryHashMap = new HashMap<>();

    @Override
    public void createDictionaryEntry(String headword, DictionaryEntry dictionaryEntry) {
        this.dictionaryEntryHashMap.put(headword, dictionaryEntry);
    }

    @Override
    public void deleteDictionaryEntry(String headword) {
        this.dictionaryEntryHashMap.remove(headword);
    }

    @Override
    public Optional<DictionaryEntry> retrieveDictionaryEntryExactMatch(String headword) {
        return Optional.ofNullable(this.dictionaryEntryHashMap.get(headword));
    }

    @Override
    public List<DictionaryEntry> retrieveDictionaryEntriesPartialMatch(String partialHeadword) {
        return List.of();
    }

    @Override
    public List<DictionaryEntry> retrieveAllDictionaryEntries() {
        return List.copyOf(this.dictionaryEntryHashMap.values());
    }

    @Override
    public Boolean contains(String headword) {
        return this.dictionaryEntryHashMap.containsKey(headword);
    }
}
