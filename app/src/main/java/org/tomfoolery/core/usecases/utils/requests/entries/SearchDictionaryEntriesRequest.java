package org.tomfoolery.core.usecases.utils.requests;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

@Value(staticConstructor = "of")
public class SearchDictionaryEntriesRequest {
    @NonNull DictionaryEntry.ID prefixOfDictionaryEntryID;
}
