package org.tomfoolery.core.usecases.entries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.utils.function.ThrowableConsumer;
import org.tomfoolery.core.usecases.utils.requests.DeleteDictionaryEntryRequest;


@RequiredArgsConstructor(staticName = "of")
public class DeleteDictionaryEntryUseCase implements ThrowableConsumer<DeleteDictionaryEntryRequest> {
    @NonNull private final DictionaryEntryRepository repository;

    @Override
    public void accept(@NonNull DeleteDictionaryEntryRequest request) throws NotFoundException {
        val dictionaryEntryID = request.getDictionaryEntryID();

        if (this.repository.contains(dictionaryEntryID))
            throw new NotFoundException();

        this.repository.delete(dictionaryEntryID);
    }

    public static class NotFoundException extends Exception {}
}
