package org.tomfoolery.configurations.monolith.terminal.utils.responses;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class AddDictionaryEntryViewResponse {
    @NonNull String headword;
}
