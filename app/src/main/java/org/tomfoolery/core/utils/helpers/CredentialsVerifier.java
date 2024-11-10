package org.tomfoolery.core.utils.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

import java.nio.CharBuffer;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class CredentialsVerifier {
    public static final @NonNull String USERNAME_REGEX = "^(?![0-9])(?!.*_$)[a-z0-9_]{8,16}$";
    public static final @NonNull String PASSWORD_REGEX = "^\\w{8,32}$";

    public static boolean verifyCredentials(BaseUser.@NonNull Credentials rawCredentials) {
        val username = rawCredentials.getUsername();
        val rawPassword = rawCredentials.getPassword();

        return verifyUsername(username)
            && verifyPassword(rawPassword);
    }

    public static boolean verifyUsername(@NonNull String username) {
        return username.matches(USERNAME_REGEX);
    }

    public static boolean verifyPassword(@NonNull SecureString rawPassword) {
        val pattern = Pattern.compile(PASSWORD_REGEX);
        val charBuffer = CharBuffer.wrap(rawPassword);

        return pattern.matcher(charBuffer).matches();
    }
}
