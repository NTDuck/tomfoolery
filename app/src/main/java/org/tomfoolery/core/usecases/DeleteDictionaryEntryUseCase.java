package org.tomfoolery.core.usecases;

import org.tomfoolery.core.repositories.DictionaryEntryRepository;

public class DeleteDictionaryEntryUseCase extends DictionaryEntryUseCase {
    public DeleteDictionaryEntryUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        super(dictionaryEntryRepository);
    }

    public void invoke(String headword) {
        this.dictionaryEntryRepository.deleteDictionaryEntry(headword);
    }
}
