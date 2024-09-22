package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.utils.exceptions.NotFoundException;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.requests.UpdateDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.UpdateDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDictionaryEntryUseCase implements ThrowableFunction<UpdateDictionaryEntryRequest, UpdateDictionaryEntryResponse> {
    private final @NonNull DictionaryEntryRepository repository;

    @Override
    public UpdateDictionaryEntryResponse apply(@NonNull UpdateDictionaryEntryRequest request) throws NotFoundException {
        val entry = request.getEntry();
        val entryID = entry.getId();

        if (!this.repository.contains(entryID))
            throw new NotFoundException();

        this.repository.save(entry);

        return UpdateDictionaryEntryResponse.of(entryID);
    }
}
