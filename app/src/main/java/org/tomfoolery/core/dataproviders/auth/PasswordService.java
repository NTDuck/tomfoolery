package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface PasswordService {
    @NonNull String encodePassword(@NonNull String password);
    boolean verifyPassword(@NonNull String password, @NonNull String encodedPassword);
}
