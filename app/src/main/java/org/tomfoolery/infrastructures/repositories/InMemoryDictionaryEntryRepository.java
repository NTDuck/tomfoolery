package org.tomfoolery.infrastructures.repositories;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class InMemoryDictionaryEntryRepository implements DictionaryEntryRepository {
    private static final int expectedNumberOfWordsInDictionary = 470_000;
    private final HashMap<String, DictionaryEntry> dictionaryEntries = HashMap.newHashMap(expectedNumberOfWordsInDictionary);

    @Override
    public void save(@NonNull DictionaryEntry dictionaryEntry) {
        this.dictionaryEntries.put(dictionaryEntry.getHeadword(), dictionaryEntry);
    }

    @Override
    public void delete(@NonNull String headword) {
        this.dictionaryEntries.remove(headword);
    }

    @Override
    public DictionaryEntry get(@NonNull String headword) throws NotFoundException {
        return this.dictionaryEntries.get(headword);
    }

    @Override
    public List<DictionaryEntry> search(@NonNull String headword) {
        return List.of();
    }

    @Override
    public List<DictionaryEntry> search() {
        return List.copyOf(this.dictionaryEntries.values());
    }

    @Override
    public boolean contains(@NonNull String headword) {
        return this.dictionaryEntries.containsKey(headword);
    }
}
