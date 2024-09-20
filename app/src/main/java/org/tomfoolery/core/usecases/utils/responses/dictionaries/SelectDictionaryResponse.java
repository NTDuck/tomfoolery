package org.tomfoolery.core.usecases.utils.responses;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Dictionary;

@Value(staticConstructor = "of")
public class SelectDictionaryResponse {
    @NonNull Dictionary dictionary;
}
