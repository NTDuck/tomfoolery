package org.tomfoolery.core.dataproviders.generators.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

public interface PasswordEncoder {
    @NonNull SecureString encodePassword(@NonNull SecureString rawPassword);
    boolean verifyPassword(@NonNull SecureString rawPassword, @NonNull SecureString encodedPassword);
}
