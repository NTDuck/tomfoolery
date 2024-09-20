package org.tomfoolery.core.usecases.dictionaries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryRepository;
import org.tomfoolery.core.usecases.utils.ThrowableFunction;
import org.tomfoolery.core.usecases.utils.requests.AddDictionaryRequest;
import org.tomfoolery.core.usecases.utils.responses.AddDictionaryResponse;

@RequiredArgsConstructor(staticName = "of")
public class AddDictionaryUseCase implements ThrowableFunction<AddDictionaryRequest, AddDictionaryResponse> {
    @NonNull private final DictionaryRepository repository;

    @Override
    public AddDictionaryResponse apply(@NonNull AddDictionaryRequest request) throws AlreadyExistsException {
        val dictionary = request.getDictionary();

        if (this.repository.contains(dictionary.getId()))
            throw new AlreadyExistsException();

        this.repository.save(dictionary);
        return AddDictionaryResponse.of(dictionary.getId());
    }

    public static class AlreadyExistsException extends Exception {}
}
