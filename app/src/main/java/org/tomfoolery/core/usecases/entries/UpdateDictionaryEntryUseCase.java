package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.domain.Dictionary;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.requests.entries.UpdateDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.entries.UpdateDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class UpdateDictionaryEntryUseCase implements ThrowableFunction<UpdateDictionaryEntryRequest, UpdateDictionaryEntryResponse> {
    @NonNull private final Dictionary dictionary;

    @Override
    public UpdateDictionaryEntryResponse apply(@NonNull UpdateDictionaryEntryRequest request) throws NotFoundException {
        val dictionaryEntry = request.getDictionaryEntry();

        if (!this.dictionary.getEntryRepository().contains(dictionaryEntry.getId()))
            throw new NotFoundException();

        this.dictionary.getEntryRepository().save(dictionaryEntry);
        return UpdateDictionaryEntryResponse.of(dictionaryEntry.getId());
    }

    public static class NotFoundException extends Exception {}
}
