package org.tomfoolery.core.usecases.dictionaries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryRepository;
import org.tomfoolery.core.utils.function.ThrowableConsumer;
import org.tomfoolery.core.utils.requests.dictionaries.DeleteDictionaryRequest;

@RequiredArgsConstructor(staticName = "of")
public class DeleteDictionaryUseCase implements ThrowableConsumer<DeleteDictionaryRequest> {
    @NonNull private final DictionaryRepository repository;

    @Override
    public void accept(@NonNull DeleteDictionaryRequest request) throws NotFoundException {
        val dictionaryID = request.getDictionaryID();

        if (!this.repository.contains(dictionaryID))
            throw new NotFoundException();

        this.repository.delete(dictionaryID);
    }

    public static class NotFoundException extends Exception {}
}
