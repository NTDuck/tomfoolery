package org.tomfoolery.core.usecases;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

import java.util.List;

public class RetrieveDictionaryEntriesPartialMatchUseCase extends DictionaryEntryUseCase {
    public RetrieveDictionaryEntriesPartialMatchUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        super(dictionaryEntryRepository);
    }

    public List<DictionaryEntry> invoke(String partialHeadword) {
        return this.dictionaryEntryRepository.retrieveDictionaryEntriesPartialMatch(partialHeadword);
    }
}
