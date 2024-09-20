package org.tomfoolery.core.usecases.utils.responses.dictionaries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Dictionary;

@Value(staticConstructor = "of")
public class AddDictionaryResponse {
    @NonNull Dictionary.ID dictionaryID;
}
