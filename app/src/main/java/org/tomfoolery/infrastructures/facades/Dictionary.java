package org.tomfoolery.infrastructures.facades;

import lombok.NonNull;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.*;
import org.tomfoolery.core.utils.exceptions.AlreadyExistsException;
import org.tomfoolery.core.utils.exceptions.NotFoundException;
import org.tomfoolery.core.utils.requests.*;
import org.tomfoolery.core.utils.responses.*;

public class Dictionary extends ReadonlyDictionary {
    private final @NonNull AddDictionaryEntryUseCase addDictionaryEntryUseCase;
    private final @NonNull UpdateDictionaryEntryUseCase updateDictionaryEntryUseCase;
    private final @NonNull DeleteDictionaryEntryUseCase deleteDictionaryEntryUseCase;

    public AddDictionaryEntryResponse addEntry(@NonNull AddDictionaryEntryRequest request) throws AlreadyExistsException {
        return this.addDictionaryEntryUseCase.apply(request);
    }

    public UpdateDictionaryEntryResponse updateEntry(@NonNull UpdateDictionaryEntryRequest request) throws NotFoundException {
        return this.updateDictionaryEntryUseCase.apply(request);
    }

    public void deleteEntry(@NonNull DeleteDictionaryEntryRequest request) throws NotFoundException {
        this.deleteDictionaryEntryUseCase.accept(request);
    }

    private Dictionary(@NonNull DictionaryEntryRepository repository) {
        super(repository);

        this.addDictionaryEntryUseCase = AddDictionaryEntryUseCase.of(repository);
        this.updateDictionaryEntryUseCase = UpdateDictionaryEntryUseCase.of(repository);
        this.deleteDictionaryEntryUseCase = DeleteDictionaryEntryUseCase.of(repository);
    }

    public static Dictionary of(@NonNull DictionaryEntryRepository repository) {
        return new Dictionary(repository);
    }
}
