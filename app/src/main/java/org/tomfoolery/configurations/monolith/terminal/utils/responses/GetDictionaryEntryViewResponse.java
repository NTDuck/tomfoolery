package org.tomfoolery.configurations.monolith.terminal.utils.responses;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class GetDictionaryEntryViewResponse {
    @NonNull String headword;
    @NonNull String[] definitions;
}
