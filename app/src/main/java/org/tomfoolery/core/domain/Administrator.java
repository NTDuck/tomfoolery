package org.tomfoolery.core.domain;

import lombok.NonNull;

public class Administrator extends ReadonlyUser {
    public Administrator(@NonNull Credentials credentials) {
        super(credentials);
    }

    public static Administrator of(@NonNull Credentials credentials) {
        return new Administrator(credentials);
    }
}
