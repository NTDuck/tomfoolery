package org.tomfoolery.core.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Administrator extends ReadonlyUser {
    public Administrator(@NonNull Credentials credentials) {
        super(credentials);
    }

    public static @NonNull Administrator of(@NonNull Credentials credentials) {
        return new Administrator(credentials);
    }
}
