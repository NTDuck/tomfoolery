package org.tomfoolery.core.usecases.dictionaries;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.tomfoolery.core.dataproviders.DictionaryRepository;
import org.tomfoolery.core.usecases.utils.responses.ShowDictionariesResponse;

import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of")
public class ShowDictionariesUseCase implements Supplier<ShowDictionariesResponse> {
    @NonNull private final DictionaryRepository repository;

    @Override
    public ShowDictionariesResponse get() {
        val dictionaries = this.repository.show();
        return ShowDictionariesResponse.of(dictionaries);
    }
}
