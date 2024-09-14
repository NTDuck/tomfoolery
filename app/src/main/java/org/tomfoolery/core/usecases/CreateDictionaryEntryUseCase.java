package org.tomfoolery.core.usecases;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

public class CreateDictionaryEntryUseCase extends DictionaryEntryUseCase {
    public CreateDictionaryEntryUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        super(dictionaryEntryRepository);
    }

    public void invoke(String headword, DictionaryEntry dictionaryEntry) {
        if (this.dictionaryEntryRepository.contains(headword))
            return;

        this.dictionaryEntryRepository.createDictionaryEntry(headword, dictionaryEntry);
    }
}
