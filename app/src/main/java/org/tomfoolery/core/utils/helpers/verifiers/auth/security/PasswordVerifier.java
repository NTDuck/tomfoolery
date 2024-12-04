package org.tomfoolery.core.utils.helpers.verifiers.auth.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class PasswordVerifier {
    public static final @NonNull String PASSWORD_REGEX = "^\\w{8,32}$";

    public static boolean verify(@NonNull SecureString password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }
}
