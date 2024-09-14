package org.tomfoolery.core.repositories;

import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.List;
import java.util.Optional;

public interface DictionaryEntryRepository {
    void createDictionaryEntry(String headword, DictionaryEntry dictionaryEntry);
    void deleteDictionaryEntry(String headword);

    Optional<DictionaryEntry> retrieveDictionaryEntryExactMatch(String headword);
    List<DictionaryEntry> retrieveDictionaryEntriesPartialMatch(String partialHeadword);
    List<DictionaryEntry> retrieveAllDictionaryEntries();

    Boolean contains(String headword);
}
