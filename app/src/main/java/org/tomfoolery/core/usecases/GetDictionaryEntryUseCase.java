package org.tomfoolery.core.usecases;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

import java.util.Optional;

public class GetDictionaryEntryUseCase {
    private final DictionaryEntryRepository dictionaryEntryRepository;

    public GetDictionaryEntryUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        this.dictionaryEntryRepository = dictionaryEntryRepository;
    }

    public Optional<DictionaryEntry> invoke(String headword) {
        return dictionaryEntryRepository.getDictionaryEntry(headword);
    }
}
