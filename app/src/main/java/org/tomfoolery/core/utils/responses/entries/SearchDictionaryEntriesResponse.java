package org.tomfoolery.core.utils.responses.entries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.List;

@Value(staticConstructor = "of")
public class SearchDictionaryEntriesResponse {
    @NonNull List<DictionaryEntry> dictionaryEntries;
}
