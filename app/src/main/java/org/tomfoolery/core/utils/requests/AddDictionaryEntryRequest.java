package org.tomfoolery.core.utils.requests;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

@Value(staticConstructor = "of")
public class AddDictionaryEntryRequest {
    @NonNull DictionaryEntry entry;
}