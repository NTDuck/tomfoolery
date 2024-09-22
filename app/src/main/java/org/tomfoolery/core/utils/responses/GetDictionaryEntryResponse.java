package org.tomfoolery.core.utils.responses;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

@Value(staticConstructor = "of")
public class GetDictionaryEntryResponse {
    @NonNull DictionaryEntry entry;
}