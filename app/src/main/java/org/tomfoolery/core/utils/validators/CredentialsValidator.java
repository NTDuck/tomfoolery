package org.tomfoolery.core.utils.validators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.ReadonlyUser;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CredentialsValidator {
    public static <User extends ReadonlyUser> boolean isCredentialsValid(User.@NonNull Credentials credentials) {
        val username = credentials.getUsername();
        val password = credentials.getPassword();

        return isUsernameValid(username)
            && isPasswordValid(password);
    }

    private static boolean isUsernameValid(@NonNull String username) {
        return username.matches("^(?![0-9])(?!.*_$)[a-z0-9_]{8,16}$");
    }

    private static boolean isPasswordValid(@NonNull String rawPassword) {
        return rawPassword.matches("^\\w{8,32}$");
    }
}