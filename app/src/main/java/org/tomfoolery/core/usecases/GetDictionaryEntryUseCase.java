package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

@RequiredArgsConstructor(staticName = "of")
public class GetDictionaryEntryUseCase {
    @NonNull
    private final DictionaryEntryRepository dictionaryEntryRepository;

    public DictionaryEntry invoke(@NonNull String headword) throws DictionaryEntryRepository.NotFoundException {
        return this.dictionaryEntryRepository.get(headword);
    }
}
