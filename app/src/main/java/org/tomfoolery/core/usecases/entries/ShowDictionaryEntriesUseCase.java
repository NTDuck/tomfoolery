package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.utils.responses.entries.ShowDictionaryEntriesResponse;

import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of")
public class ShowDictionaryEntriesUseCase implements Supplier<ShowDictionaryEntriesResponse> {
    @NonNull private final DictionaryEntryRepository repository;

    @Override
    public ShowDictionaryEntriesResponse get() {
        val dictionaryEntries = this.repository.show();
        return ShowDictionaryEntriesResponse.of(dictionaryEntries);
    }
}
