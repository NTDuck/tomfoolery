package org.tomfoolery.core.dataproviders.generators.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseGenerator;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public interface PasswordEncoder extends BaseGenerator {
    @NonNull SecureString encodePassword(@NonNull SecureString rawPassword);

    default boolean verifyPassword(@NonNull SecureString rawPassword, @NonNull SecureString encodedPassword) {
        return this.encodePassword(rawPassword).equals(encodedPassword);
    }
}
