package org.tomfoolery.core.domain.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

public final class Administrator extends BaseUser {
    public static @NonNull Administrator of(@NonNull Id id, @NonNull Credentials credentials, @NonNull Audit audit) {
        return new Administrator(id, credentials, audit);
    }

    private Administrator(@NonNull Id id, @NonNull Credentials credentials, @NonNull Audit audit) {
        super(id, credentials, audit);
    }
}
