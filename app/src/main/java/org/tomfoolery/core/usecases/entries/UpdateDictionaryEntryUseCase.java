package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.utils.function.ThrowableFunction;
import org.tomfoolery.core.usecases.utils.requests.entries.UpdateDictionaryEntryRequest;
import org.tomfoolery.core.usecases.utils.responses.entries.UpdateDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDictionaryEntryUseCase implements ThrowableFunction<UpdateDictionaryEntryRequest, UpdateDictionaryEntryResponse> {
    @NonNull private final DictionaryEntryRepository repository;

    @Override
    public UpdateDictionaryEntryResponse apply(@NonNull UpdateDictionaryEntryRequest request) throws NotFoundException {
        val dictionaryEntry = request.getDictionaryEntry();

        if (!this.repository.contains(dictionaryEntry.getId()))
            throw new NotFoundException();

        this.repository.save(dictionaryEntry);
        return UpdateDictionaryEntryResponse.of(dictionaryEntry.getId());
    }

    public static class NotFoundException extends Exception {}
}
