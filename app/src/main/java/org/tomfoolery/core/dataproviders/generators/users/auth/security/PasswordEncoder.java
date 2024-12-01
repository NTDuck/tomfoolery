package org.tomfoolery.core.dataproviders.generators.users.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseGenerator;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public interface PasswordEncoder extends BaseGenerator {
    @NonNull SecureString encode(@NonNull SecureString rawPassword);

    default boolean verify(@NonNull SecureString rawPassword, @NonNull SecureString encodedPassword) {
        return this.encode(rawPassword).equals(encodedPassword);
    }
}
