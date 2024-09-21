package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.domain.Dictionary;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.requests.entries.GetDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.entries.GetDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class GetDictionaryEntryUseCase implements ThrowableFunction<GetDictionaryEntryRequest, GetDictionaryEntryResponse> {
    @NonNull private final Dictionary dictionary;

    @Override
    public GetDictionaryEntryResponse apply(@NonNull GetDictionaryEntryRequest request) throws NotFoundException {
        val dictionaryEntryID = request.getDictionaryEntryID();

        if (!this.dictionary.getEntryRepository().contains(dictionaryEntryID))
            throw new NotFoundException();

        val dictionary = this.dictionary.getEntryRepository().get(dictionaryEntryID);
        return GetDictionaryEntryResponse.of(dictionary);
    }

    public static class NotFoundException extends Exception {}
}
