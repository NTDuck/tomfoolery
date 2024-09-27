package org.tomfoolery.infrastructures.dataproviders;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.domain.DictionaryEntry;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

@NoArgsConstructor(staticName = "of")
public class InMemoryDictionaryEntryRepository implements DictionaryEntryRepository {
    // Uncomment this when implementation of Trie is available
    //    private final @NonNull Trie<DictionaryEntry> entries = Trie.of();
    private final @NonNull HashMap<String, DictionaryEntry> entries = new HashMap<>();

    @Override
    public void save(@NonNull DictionaryEntry entry) {
        val headword = entry.getId().getHeadword();
        this.entries.put(headword, entry);
    }

    @Override
    public void delete(@NonNull DictionaryEntry.ID entryID) {
        val headword = entryID.getHeadword();
        this.entries.remove(headword);
    }

    @Override
    public DictionaryEntry get(@NonNull DictionaryEntry.ID entryID) {
        val headword = entryID.getHeadword();
        return this.entries.get(headword);
    }

    @Override
    public Collection<DictionaryEntry> search(@NonNull DictionaryEntry.ID prefixOfEntryID) {
        return List.of();
    }

    @Override
    public Collection<DictionaryEntry> show() {
        return this.entries.values();
    }

    @Override
    public boolean contains(@NonNull DictionaryEntry.ID entryID) {
        val headword = entryID.getHeadword();
        return this.entries.containsKey(headword);
    }
}
