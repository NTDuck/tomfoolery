package org.tomfoolery.core.dataproviders;

import lombok.NonNull;
import org.jmolecules.ddd.annotation.Repository;
import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.List;

@Repository
public interface DictionaryEntryRepository {
    void save(@NonNull DictionaryEntry dictionaryEntry);
    void delete(@NonNull DictionaryEntry.ID dictionaryEntryID);

    DictionaryEntry get(@NonNull DictionaryEntry.ID dictionaryEntryID);
    List<DictionaryEntry> search(@NonNull DictionaryEntry.ID prefixOfDictionaryEntryID);
    List<DictionaryEntry> show();

    boolean contains(@NonNull DictionaryEntry.ID dictionaryEntryID);
}
