package org.tomfoolery.core.utils.helpers.verifiers.users.patron;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class PhoneNumberVerifier {
    private static final @NonNull String REGEX = "\\+?[0-9\\-\\s]{7,15}";

    public static boolean verify(@NonNull String phoneNumber) {
        return phoneNumber.matches(REGEX);
    }
}
