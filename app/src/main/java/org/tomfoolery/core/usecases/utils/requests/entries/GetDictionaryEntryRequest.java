package org.tomfoolery.core.usecases.utils.requests.entries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

@Value(staticConstructor = "of")
public class GetDictionaryEntryRequest {
    @NonNull DictionaryEntry.ID dictionaryEntryID;
}
