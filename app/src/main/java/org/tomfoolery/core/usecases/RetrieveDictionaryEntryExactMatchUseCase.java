package org.tomfoolery.core.usecases;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

import java.util.Optional;

public class RetrieveDictionaryEntryExactMatchUseCase extends DictionaryEntryUseCase {
    public RetrieveDictionaryEntryExactMatchUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        super(dictionaryEntryRepository);
    }

    public Optional<DictionaryEntry> invoke(String headword) {
        return dictionaryEntryRepository.retrieveDictionaryEntryExactMatch(headword);
    }
}
