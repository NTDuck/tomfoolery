package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.utils.requests.SearchDictionaryEntriesRequest;
import org.tomfoolery.core.usecases.utils.responses.SearchDictionaryEntriesResponse;

import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
public class SearchDictionaryEntriesUseCase implements Function<SearchDictionaryEntriesRequest, SearchDictionaryEntriesResponse> {
    @NonNull private final DictionaryEntryRepository repository;

    @Override
    public SearchDictionaryEntriesResponse apply(@NonNull SearchDictionaryEntriesRequest request) {
        val prefixOfDictionaryEntryID = request.getPrefixOfDictionaryEntryID();
        val dictionaryEntries = this.repository.search(prefixOfDictionaryEntryID);
        return SearchDictionaryEntriesResponse.of(dictionaryEntries);
    }
}
