package org.tomfoolery.core.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Staff extends ReadonlyUser {
    public Staff(@NonNull Credentials credentials) {
        super(credentials);
    }

    public static @NonNull Staff of(@NonNull Credentials credentials) {
        return new Staff(credentials);
    }
}
