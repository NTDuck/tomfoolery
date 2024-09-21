package org.tomfoolery.core.usecases.utils.requests.dictionaries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Dictionary;

@Value(staticConstructor = "of")
public class DeleteDictionaryRequest {
    @NonNull Dictionary.ID dictionaryID;
}
