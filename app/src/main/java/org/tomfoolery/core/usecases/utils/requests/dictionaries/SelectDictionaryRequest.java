package org.tomfoolery.core.usecases.utils.requests;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Dictionary;

@Value(staticConstructor = "of")
public class SelectDictionaryRequest {
    @NonNull Dictionary.ID dictionaryID;
}
