package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.utils.responses.ShowDictionaryEntriesResponse;

import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of")
public class ShowDictionaryEntriesUseCase implements Supplier<ShowDictionaryEntriesResponse> {
    private final @NonNull DictionaryEntryRepository repository;

    @Override
    public ShowDictionaryEntriesResponse get() {
        val entries = this.repository.show();
        return ShowDictionaryEntriesResponse.of(entries);
    }
}
