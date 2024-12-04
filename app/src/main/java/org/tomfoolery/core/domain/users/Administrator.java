package org.tomfoolery.core.domain.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.abc.BaseUser;

public final class Administrator extends BaseUser {
    public static @NonNull Administrator of(@NonNull Id id, @NonNull Audit audit, @NonNull Credentials credentials) {
        return new Administrator(id, audit, credentials);
    }

    private Administrator(@NonNull Id id, @NonNull Audit audit, @NonNull Credentials credentials) {
        super(id, audit, credentials);
    }
}
