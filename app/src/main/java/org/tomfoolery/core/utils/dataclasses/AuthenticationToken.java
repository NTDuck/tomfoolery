package org.tomfoolery.core.utils.dataclasses;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

@Value(staticConstructor = "of")
public class AuthenticationToken {
    @NonNull String serializedPayload;
}
