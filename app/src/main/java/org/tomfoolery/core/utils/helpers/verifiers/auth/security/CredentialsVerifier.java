package org.tomfoolery.core.utils.helpers.verifiers.auth.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.helpers.verifiers.auth.UsernameVerifier;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class CredentialsVerifier {
    public static boolean verify(BaseUser.@NonNull Credentials credentials) {
        return UsernameVerifier.verify(credentials.getUsername())
            && PasswordVerifier.verify(credentials.getPassword());
    }
}
