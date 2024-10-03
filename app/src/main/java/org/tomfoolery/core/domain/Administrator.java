package org.tomfoolery.core.domain;

import lombok.NonNull;

public class Administrator extends User {
    public Administrator(@NonNull Credentials credentials, @NonNull Timestamps timestamps) {
        super(credentials, timestamps);
    }

    public static Administrator of(@NonNull Credentials credentials, @NonNull Timestamps timestamps) {
        return new Administrator(credentials, timestamps);
    }
}
