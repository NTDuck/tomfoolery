package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.utils.function.ThrowableFunction;
import org.tomfoolery.core.usecases.utils.requests.entries.AddDictionaryEntryRequest;
import org.tomfoolery.core.usecases.utils.responses.entries.AddDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class AddDictionaryEntryUseCase implements ThrowableFunction<AddDictionaryEntryRequest, AddDictionaryEntryResponse> {
    @NonNull private final DictionaryEntryRepository repository;

    @Override
    public AddDictionaryEntryResponse apply(@NonNull AddDictionaryEntryRequest request) throws AlreadyExistsException {
        val dictionaryEntry = request.getDictionaryEntry();

        if (this.repository.contains(dictionaryEntry.getId()))
            throw new AlreadyExistsException();

        this.repository.save(dictionaryEntry);
        return AddDictionaryEntryResponse.of(dictionaryEntry.getId());
    }

    public static class AlreadyExistsException extends Exception {}
}
