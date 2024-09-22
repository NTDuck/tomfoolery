package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.utils.requests.SearchDictionaryEntriesRequest;
import org.tomfoolery.core.utils.responses.SearchDictionaryEntriesResponse;

import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
public class SearchDictionaryEntriesUseCase implements Function<SearchDictionaryEntriesRequest, SearchDictionaryEntriesResponse> {
    private final @NonNull DictionaryEntryRepository repository;

    @Override
    public SearchDictionaryEntriesResponse apply(@NonNull SearchDictionaryEntriesRequest request) {
        val prefixOfEntryID = request.getPrefixOfEntryID();
        val entries = this.repository.search(prefixOfEntryID);

        return SearchDictionaryEntriesResponse.of(entries);
    }
}
