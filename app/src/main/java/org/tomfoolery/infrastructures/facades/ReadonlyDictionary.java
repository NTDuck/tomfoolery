package org.tomfoolery.infrastructures.facades;

import lombok.NonNull;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.GetDictionaryEntryUseCase;
import org.tomfoolery.core.usecases.SearchDictionaryEntriesUseCase;
import org.tomfoolery.core.usecases.ShowDictionaryEntriesUseCase;
import org.tomfoolery.core.utils.exceptions.NotFoundException;
import org.tomfoolery.core.utils.requests.GetDictionaryEntryRequest;
import org.tomfoolery.core.utils.requests.SearchDictionaryEntriesRequest;
import org.tomfoolery.core.utils.responses.GetDictionaryEntryResponse;
import org.tomfoolery.core.utils.responses.SearchDictionaryEntriesResponse;
import org.tomfoolery.core.utils.responses.ShowDictionaryEntriesResponse;

public class ReadonlyDictionary {
    private final @NonNull GetDictionaryEntryUseCase getDictionaryEntryUseCase;
    private final @NonNull SearchDictionaryEntriesUseCase searchDictionaryEntriesUseCase;
    private final @NonNull ShowDictionaryEntriesUseCase showDictionaryEntriesUseCase;

    public GetDictionaryEntryResponse getEntry(@NonNull GetDictionaryEntryRequest request) throws NotFoundException {
        return this.getDictionaryEntryUseCase.apply(request);
    }

    public SearchDictionaryEntriesResponse searchEntries(@NonNull SearchDictionaryEntriesRequest request) {
        return this.searchDictionaryEntriesUseCase.apply(request);
    }

    public ShowDictionaryEntriesResponse showEntries() {
        return this.showDictionaryEntriesUseCase.get();
    }

    protected ReadonlyDictionary(@NonNull DictionaryEntryRepository repository) {
        this.getDictionaryEntryUseCase = GetDictionaryEntryUseCase.of(repository);
        this.searchDictionaryEntriesUseCase = SearchDictionaryEntriesUseCase.of(repository);
        this.showDictionaryEntriesUseCase = ShowDictionaryEntriesUseCase.of(repository);
    }

    public static ReadonlyDictionary of(@NonNull DictionaryEntryRepository repository) {
        return new ReadonlyDictionary(repository);
    }
}
