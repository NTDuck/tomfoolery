package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

@RequiredArgsConstructor(staticName = "of")
public class AddDictionaryEntryUseCase {
    @NonNull
    private final DictionaryEntryRepository dictionaryEntryRepository;

    public void invoke(@NonNull DictionaryEntry dictionaryEntry) {
        if (this.dictionaryEntryRepository.contains(dictionaryEntry.getHeadword()))
            return;

        this.dictionaryEntryRepository.save(dictionaryEntry);
    }
}
