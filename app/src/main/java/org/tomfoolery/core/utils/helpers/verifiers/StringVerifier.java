package org.tomfoolery.core.utils.helpers.verifiers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class StringVerifier {
    public static boolean verify(@NonNull String string) {
        return !string.trim().isEmpty();
    }
}
