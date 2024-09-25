package org.tomfoolery.configurations.monolith.terminal.utils.requests;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class MenuUserSubmission {
    @NonNull String selection;
}
