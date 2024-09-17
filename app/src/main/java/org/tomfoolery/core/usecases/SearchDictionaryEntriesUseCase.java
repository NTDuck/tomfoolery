package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class SearchDictionaryEntriesUseCase {
    @NonNull
    private final DictionaryEntryRepository dictionaryEntryRepository;

    public List<DictionaryEntry> invoke() {
        return this.dictionaryEntryRepository.search();
    }

    public List<DictionaryEntry> invoke(@NonNull String headword) {
        return this.dictionaryEntryRepository.search(headword);
    }
}
