package org.tomfoolery.configurations.monolith.terminal.utils.requests;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class AddDictionaryEntryUserSubmission {
    @NonNull String headword;
    @NonNull String definitions;
}
