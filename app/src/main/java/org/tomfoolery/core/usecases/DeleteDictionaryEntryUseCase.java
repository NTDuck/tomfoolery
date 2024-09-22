package org.tomfoolery.core.usecases;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;
import org.tomfoolery.core.utils.exceptions.NotFoundException;
import org.tomfoolery.core.utils.function.ThrowableConsumer;
import org.tomfoolery.core.utils.requests.DeleteDictionaryEntryRequest;

@RequiredArgsConstructor(staticName = "of")
public class DeleteDictionaryEntryUseCase implements ThrowableConsumer<DeleteDictionaryEntryRequest> {
    private final @NonNull DictionaryEntryRepository repository;

    @Override
    public void accept(@NonNull DeleteDictionaryEntryRequest request) throws NotFoundException {
        val entryID = request.getEntryID();

        if (!this.repository.contains(entryID))
            throw new NotFoundException();

        this.repository.delete(entryID);
    }
}
