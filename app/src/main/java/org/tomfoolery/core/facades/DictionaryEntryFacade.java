package org.tomfoolery.core.facades;

import lombok.NonNull;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.*;
import org.tomfoolery.core.utils.exceptions.AlreadyExistsException;
import org.tomfoolery.core.utils.exceptions.NotFoundException;
import org.tomfoolery.core.utils.requests.*;
import org.tomfoolery.core.utils.responses.*;

public class DictionaryEntryFacade {
    private final @NonNull AddDictionaryEntryUseCase addDictionaryEntryUseCase;
    private final @NonNull GetDictionaryEntryUseCase getDictionaryEntryUseCase;
    private final @NonNull SearchDictionaryEntriesUseCase searchDictionaryEntriesUseCase;
    private final @NonNull ShowDictionaryEntriesUseCase showDictionaryEntriesUseCase;
    private final @NonNull UpdateDictionaryEntryUseCase updateDictionaryEntryUseCase;
    private final @NonNull DeleteDictionaryEntryUseCase deleteDictionaryEntryUseCase;

    public AddDictionaryEntryResponse addEntry(@NonNull AddDictionaryEntryRequest request) throws AlreadyExistsException {
        return this.addDictionaryEntryUseCase.apply(request);
    }

    public GetDictionaryEntryResponse getEntry(@NonNull GetDictionaryEntryRequest request) throws NotFoundException {
        return this.getDictionaryEntryUseCase.apply(request);
    }

    public SearchDictionaryEntriesResponse searchEntries(@NonNull SearchDictionaryEntriesRequest request) {
        return this.searchDictionaryEntriesUseCase.apply(request);
    }

    public ShowDictionaryEntriesResponse showEntries() {
        return this.showDictionaryEntriesUseCase.get();
    }

    public UpdateDictionaryEntryResponse updateEntry(@NonNull UpdateDictionaryEntryRequest request) throws NotFoundException {
        return this.updateDictionaryEntryUseCase.apply(request);
    }

    public void deleteEntry(@NonNull DeleteDictionaryEntryRequest request) throws NotFoundException {
        this.deleteDictionaryEntryUseCase.accept(request);
    }

    private DictionaryEntryFacade(DictionaryEntryRepository repository) {
        this.addDictionaryEntryUseCase = AddDictionaryEntryUseCase.of(repository);
        this.getDictionaryEntryUseCase = GetDictionaryEntryUseCase.of(repository);
        this.searchDictionaryEntriesUseCase = SearchDictionaryEntriesUseCase.of(repository);
        this.showDictionaryEntriesUseCase = ShowDictionaryEntriesUseCase.of(repository);
        this.updateDictionaryEntryUseCase = UpdateDictionaryEntryUseCase.of(repository);
        this.deleteDictionaryEntryUseCase = DeleteDictionaryEntryUseCase.of(repository);
    }

    public static DictionaryEntryFacade of(DictionaryEntryRepository repository) {
        return new DictionaryEntryFacade(repository);
    }
}
