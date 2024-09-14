package org.tomfoolery.core.usecases;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

import java.util.List;

public class RetrieveAllDictionaryEntriesUseCase extends DictionaryEntryUseCase {
    public RetrieveAllDictionaryEntriesUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        super(dictionaryEntryRepository);
    }

    public List<DictionaryEntry> invoke() {
        return this.dictionaryEntryRepository.retrieveAllDictionaryEntries();
    }
}
