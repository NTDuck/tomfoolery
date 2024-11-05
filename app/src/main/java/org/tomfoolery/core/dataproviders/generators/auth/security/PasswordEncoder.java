package org.tomfoolery.core.dataproviders.generators.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface PasswordEncoder {
    @NonNull String encodePassword(@NonNull String password);
    boolean verifyPassword(@NonNull String password, @NonNull String encodedPassword);
}
