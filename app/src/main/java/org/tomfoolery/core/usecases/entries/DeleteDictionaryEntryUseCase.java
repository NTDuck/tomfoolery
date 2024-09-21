package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.domain.Dictionary;
import org.tomfoolery.core.utils.function.ThrowableConsumer;
import org.tomfoolery.core.utils.requests.entries.DeleteDictionaryEntryRequest;


@RequiredArgsConstructor(staticName = "of")
public class DeleteDictionaryEntryUseCase implements ThrowableConsumer<DeleteDictionaryEntryRequest> {
    @NonNull private final Dictionary dictionary;

    @Override
    public void accept(@NonNull DeleteDictionaryEntryRequest request) throws NotFoundException {
        val dictionaryEntryID = request.getDictionaryEntryID();

        if (this.dictionary.getEntryRepository().contains(dictionaryEntryID))
            throw new NotFoundException();

        this.dictionary.getEntryRepository().delete(dictionaryEntryID);
    }

    public static class NotFoundException extends Exception {}
}
