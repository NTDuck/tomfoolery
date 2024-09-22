package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.utils.exceptions.AlreadyExistsException;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.requests.AddDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.AddDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class AddDictionaryEntryUseCase implements ThrowableFunction<AddDictionaryEntryRequest, AddDictionaryEntryResponse> {
    private final @NonNull DictionaryEntryRepository repository;

    @Override
    @SneakyThrows
    public AddDictionaryEntryResponse apply(@NonNull AddDictionaryEntryRequest request) {
        val entry = request.getEntry();
        val entryID = entry.getId();

        if (this.repository.contains(entryID))
            throw new AlreadyExistsException();

        this.repository.save(entry);

        return AddDictionaryEntryResponse.of(entryID);
    }
}