package org.tomfoolery.core.domain;

import lombok.NonNull;

public class Staff extends ReadonlyUser {
    public Staff(@NonNull Credentials credentials) {
        super(credentials);
    }

    public static Staff of(@NonNull Credentials credentials) {
        return new Staff(credentials);
    }
}
