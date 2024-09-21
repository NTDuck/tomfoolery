package org.tomfoolery.core.usecases.utils.responses.dictionaries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Dictionary;

@Value(staticConstructor = "of")
public class UpdateDictionaryResponse {
    @NonNull Dictionary.ID dictionaryID;
}