package org.tomfoolery.core.usecases.utils.requests.entries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

@Value(staticConstructor = "of")
public class AddDictionaryEntryRequest {
    @NonNull DictionaryEntry dictionaryEntry;
}