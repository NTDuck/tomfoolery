package org.tomfoolery.core.utils.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class CredentialsVerifier {
    public static <User extends BaseUser> boolean verifyCredentials(User.@NonNull Credentials credentials) {
        val username = credentials.getUsername();
        val password = credentials.getPassword();

        return verifyUsername(username)
            && verifyPassword(password);
    }

    public static boolean verifyUsername(@NonNull String username) {
        return username.matches("^(?![0-9])(?!.*_$)[a-z0-9_]{8,16}$");
    }

    public static boolean verifyPassword(@NonNull String password) {
        return password.matches("^\\w{8,32}$");
    }
}
