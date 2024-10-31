package org.tomfoolery.core.domain;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.abc.ReadonlyUser;

public class Administrator extends ReadonlyUser {
    public Administrator(@NonNull Credentials credentials) {
        super(credentials, Audit.of(Audit.Timestamps.of()));
    }

    public static @NonNull Administrator of(@NonNull Credentials credentials) {
        return new Administrator(credentials);
    }
}
