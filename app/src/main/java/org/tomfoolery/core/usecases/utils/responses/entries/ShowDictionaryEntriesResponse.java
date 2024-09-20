package org.tomfoolery.core.usecases.utils.responses.entries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.List;

@Value(staticConstructor = "of")
public class ShowDictionaryEntriesResponse {
    @NonNull List<DictionaryEntry> dictionaryEntries;
}
