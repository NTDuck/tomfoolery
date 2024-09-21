package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.domain.Dictionary;
import org.tomfoolery.core.utils.function.ThrowableFunction;
import org.tomfoolery.core.utils.requests.entries.AddDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.entries.AddDictionaryEntryResponse;

@RequiredArgsConstructor(staticName = "of")
public class AddDictionaryEntryUseCase implements ThrowableFunction<AddDictionaryEntryRequest, AddDictionaryEntryResponse> {
    @NonNull private final Dictionary dictionary;

    @Override
    public AddDictionaryEntryResponse apply(@NonNull AddDictionaryEntryRequest request) throws AlreadyExistsException {
        val dictionaryEntry = request.getDictionaryEntry();

        if (this.dictionary.getEntryRepository().contains(dictionaryEntry.getId()))
            throw new AlreadyExistsException();

        this.dictionary.getEntryRepository().save(dictionaryEntry);
        return AddDictionaryEntryResponse.of(dictionaryEntry.getId());
    }

    public static class AlreadyExistsException extends Exception {}
}
