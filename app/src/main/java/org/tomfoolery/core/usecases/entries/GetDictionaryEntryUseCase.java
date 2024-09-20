package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.utils.function.ThrowableFunction;
import org.tomfoolery.core.usecases.utils.requests.entries.GetDictionaryEntryRequest;
import org.tomfoolery.core.usecases.utils.responses.entries.GetDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class GetDictionaryEntryUseCase implements ThrowableFunction<GetDictionaryEntryRequest, GetDictionaryEntryResponse> {
    @NonNull private final DictionaryEntryRepository repository;

    @Override
    public GetDictionaryEntryResponse apply(@NonNull GetDictionaryEntryRequest request) throws NotFoundException {
        val dictionaryEntryID = request.getDictionaryEntryID();

        if (!this.repository.contains(dictionaryEntryID))
            throw new NotFoundException();

        val dictionary = this.repository.get(dictionaryEntryID);
        return GetDictionaryEntryResponse.of(dictionary);
    }

    public static class NotFoundException extends Exception {}
}
