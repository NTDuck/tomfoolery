package org.tomfoolery.core.dataproviders;

import lombok.NonNull;
import org.jmolecules.ddd.annotation.Repository;
import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.List;

/**
 * Combines the best of both worlds.<br>
 * <code>DictionaryEntryRepository</code> is used by aggregate root <code>Dictionary</code>
 * to manage persistence of its entities, <code>DictionaryEntry</code>.<br>
 * It adheres to DDD by forcing an aggregate root to manage the persistence of its entities
 * within its transactional boundaries.
 * It also adheres to Clean Architecture by moving implementation details
 * to the infrastructures layer.
 */
@Repository
public interface DictionaryEntryRepository {
    void save(@NonNull DictionaryEntry dictionaryEntry);
    void delete(@NonNull DictionaryEntry.ID dictionaryEntryID);

    DictionaryEntry get(@NonNull DictionaryEntry.ID dictionaryEntryID);
    List<DictionaryEntry> search(@NonNull DictionaryEntry.ID prefixOfDictionaryEntryID);
    List<DictionaryEntry> show();

    boolean contains(@NonNull DictionaryEntry.ID dictionaryEntryID);
}
