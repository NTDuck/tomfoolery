package org.tomfoolery.core.repositories;

import lombok.NonNull;
import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.List;

public interface DictionaryEntryRepository {
    class NotFoundException extends Exception {}

    void save(@NonNull DictionaryEntry dictionaryEntry);
    void delete(@NonNull String headword);

    DictionaryEntry get(@NonNull String headword) throws NotFoundException;
    List<DictionaryEntry> search(@NonNull String headword);
    List<DictionaryEntry> search();

    boolean contains(@NonNull String headword);
}
