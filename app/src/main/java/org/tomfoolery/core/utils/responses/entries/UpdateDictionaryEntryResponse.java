package org.tomfoolery.core.utils.responses.entries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

@Value(staticConstructor = "of")
public class UpdateDictionaryEntryResponse {
    @NonNull DictionaryEntry.ID dictionaryEntryID;
}
