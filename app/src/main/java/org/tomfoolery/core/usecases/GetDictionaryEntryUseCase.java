package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.utils.exceptions.NotFoundException;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.requests.GetDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.GetDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class GetDictionaryEntryUseCase implements ThrowableFunction<GetDictionaryEntryRequest, GetDictionaryEntryResponse> {
    private final @NonNull DictionaryEntryRepository repository;

    @Override
    public GetDictionaryEntryResponse apply(@NonNull GetDictionaryEntryRequest request) throws NotFoundException {
        val entryID = request.getEntryID();

        if (!this.repository.contains(entryID))
            throw new NotFoundException();

        val entry = this.repository.get(entryID);

        return GetDictionaryEntryResponse.of(entry);
    }
}
