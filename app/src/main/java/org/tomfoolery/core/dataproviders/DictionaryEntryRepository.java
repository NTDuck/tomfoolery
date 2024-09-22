package org.tomfoolery.core.dataproviders;

import lombok.NonNull;
import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.Collection;

public interface DictionaryEntryRepository {
    void save(@NonNull DictionaryEntry entry);
    void delete(@NonNull DictionaryEntry.ID entryID);

    DictionaryEntry get(@NonNull DictionaryEntry.ID entryID);
    Collection<DictionaryEntry> search(@NonNull DictionaryEntry.ID prefixOfEntryID);
    Collection<DictionaryEntry> show();

    boolean contains(@NonNull DictionaryEntry.ID entryID);
}
