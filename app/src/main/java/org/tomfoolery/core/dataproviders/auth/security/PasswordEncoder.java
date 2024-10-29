package org.tomfoolery.core.dataproviders.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface PasswordEncoder {
    @NonNull String encode(@NonNull String password);
    boolean verify(@NonNull String password, @NonNull String encodedPassword);
}
