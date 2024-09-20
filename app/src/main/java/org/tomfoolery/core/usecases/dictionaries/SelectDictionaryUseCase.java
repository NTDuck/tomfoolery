package org.tomfoolery.core.usecases.dictionaries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryRepository;
import org.tomfoolery.core.usecases.utils.function.ThrowableFunction;
import org.tomfoolery.core.usecases.utils.requests.SelectDictionaryRequest;
import org.tomfoolery.core.usecases.utils.responses.SelectDictionaryResponse;

@RequiredArgsConstructor(staticName = "of")
public class SelectDictionaryUseCase implements ThrowableFunction<SelectDictionaryRequest, SelectDictionaryResponse> {
    @NonNull private final DictionaryRepository repository;

    @Override
    public SelectDictionaryResponse apply(@NonNull SelectDictionaryRequest request) throws NotFoundException {
        val dictionaryID = request.getDictionaryID();

        if (!this.repository.contains(dictionaryID))
            throw new NotFoundException();

        val dictionary = this.repository.select(dictionaryID);
        return SelectDictionaryResponse.of(dictionary);
    }

    public static class NotFoundException extends Exception {}
}
