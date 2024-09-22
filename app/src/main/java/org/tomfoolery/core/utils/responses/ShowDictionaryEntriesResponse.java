package org.tomfoolery.core.utils.responses;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.Collection;

@Value(staticConstructor = "of")
public class ShowDictionaryEntriesResponse {
    @NonNull
    Collection<DictionaryEntry> entries;
}