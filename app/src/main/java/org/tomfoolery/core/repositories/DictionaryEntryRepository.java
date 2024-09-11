package org.tomfoolery.core.repositories;

import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.Optional;

public interface DictionaryEntryRepository {
    Optional<DictionaryEntry> getDictionaryEntry(String headword);
    void addDictionaryEntry(DictionaryEntry dictionaryEntry);
}
