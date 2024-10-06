package org.tomfoolery.core.domain;

import lombok.NonNull;

public class Administrator extends ReadonlyUser {
    public Administrator(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Timestamps timestamps) {
        super(credentials, audit, timestamps);
    }

    public static Administrator of(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Timestamps timestamps) {
        return new Administrator(credentials, audit, timestamps);
    }
}
