package org.tomfoolery.core.usecases.utils.request;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.tomfoolery.core.domain.Dictionary;

@Value
@RequiredArgsConstructor(staticName = "of")
public class AddDictionaryRequest {
    @NonNull Dictionary dictionary;
}
