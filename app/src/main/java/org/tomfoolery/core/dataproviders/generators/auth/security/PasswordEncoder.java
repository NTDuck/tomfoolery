package org.tomfoolery.core.dataproviders.generators.auth.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public interface PasswordEncoder {
    @NonNull SecureString encodePassword(@NonNull SecureString rawPassword);

    default BaseUser.@NonNull Credentials encodeCredentials(BaseUser.@NonNull Credentials rawCredentials) {
        val rawPassword = rawCredentials.getPassword();
        val encodedPassword = this.encodePassword(rawPassword);

        return rawCredentials.withPassword(encodedPassword);
    }

    default boolean verifyPassword(@NonNull SecureString rawPassword, @NonNull SecureString encodedPassword {
        return this.encodePassword(rawPassword).equals(encodedPassword);
    }
}
