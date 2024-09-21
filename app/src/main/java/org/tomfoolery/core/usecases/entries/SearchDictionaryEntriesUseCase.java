package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.domain.Dictionary;
import org.tomfoolery.core.utils.requests.entries.SearchDictionaryEntriesRequest;
import org.tomfoolery.core.utils.responses.entries.SearchDictionaryEntriesResponse;

import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
public class SearchDictionaryEntriesUseCase implements Function<SearchDictionaryEntriesRequest, SearchDictionaryEntriesResponse> {
    @NonNull private final Dictionary dictionary;

    @Override
    public SearchDictionaryEntriesResponse apply(@NonNull SearchDictionaryEntriesRequest request) {
        val prefixOfDictionaryEntryID = request.getPrefixOfDictionaryEntryID();
        val dictionaryEntries = this.dictionary.getEntryRepository()
            .search(prefixOfDictionaryEntryID);
        return SearchDictionaryEntriesResponse.of(dictionaryEntries);
    }
}
