package org.tomfoolery.core.utils.helpers.verifiers.users;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class UsernameVerifier {
    private static final @NonNull String REGEX = "^(?![0-9])(?!.*_$)[a-z0-9_]{8,16}$";

    public static boolean verify(@NonNull String username) {
        return username.matches(REGEX);
    }
}
