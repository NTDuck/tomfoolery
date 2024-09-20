package org.tomfoolery.core.usecases.utils.responses;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

@Value(staticConstructor = "of")
public class AddDictionaryEntryResponse {
    @NonNull DictionaryEntry.ID dictionaryEntryID;
}
