package org.tomfoolery.core.usecases;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

public class AddDictionaryEntryUseCase {
    private final DictionaryEntryRepository dictionaryEntryRepository;

    public AddDictionaryEntryUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        this.dictionaryEntryRepository = dictionaryEntryRepository;
    }

    public void invoke(DictionaryEntry dictionaryEntry) {
        dictionaryEntryRepository.addDictionaryEntry(dictionaryEntry);
    }
}
