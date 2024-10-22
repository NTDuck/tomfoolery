package org.tomfoolery.core.utils.services;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.abc.ReadonlyUser;

public interface CredentialsVerificationService {
    static <User extends ReadonlyUser> boolean verifyCredentials(User.@NonNull Credentials credentials) {
        val username = credentials.getUsername();
        val password = credentials.getPassword();

        return verifyUsername(username)
            && verifyPassword(password);
    }

    private static boolean verifyUsername(@NonNull String username) {
        return username.matches("^(?![0-9])(?!.*_$)[a-z0-9_]{8,16}$");
    }

    private static boolean verifyPassword(@NonNull String password) {
        return password.matches("^\\w{8,32}$");
    }
}
