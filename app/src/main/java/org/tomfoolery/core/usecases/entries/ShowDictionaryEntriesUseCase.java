package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.domain.Dictionary;
import org.tomfoolery.core.utils.responses.entries.ShowDictionaryEntriesResponse;

import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of")
public class ShowDictionaryEntriesUseCase implements Supplier<ShowDictionaryEntriesResponse> {
    @NonNull private final Dictionary dictionary;

    @Override
    public ShowDictionaryEntriesResponse get() {
        val dictionaryEntries = this.dictionary.getEntryRepository().show();
        return ShowDictionaryEntriesResponse.of(dictionaryEntries);
    }
}
