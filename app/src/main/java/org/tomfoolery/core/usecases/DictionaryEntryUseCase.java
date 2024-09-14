package org.tomfoolery.core.usecases;

import org.tomfoolery.core.repositories.DictionaryEntryRepository;

/**
 * Derived classes should implement method <code>invoke</code> with preferred signature.
 */
public abstract class DictionaryEntryUseCase {
    protected final DictionaryEntryRepository dictionaryEntryRepository;

    public DictionaryEntryUseCase(DictionaryEntryRepository dictionaryEntryRepository) {
        this.dictionaryEntryRepository = dictionaryEntryRepository;
    }
}
