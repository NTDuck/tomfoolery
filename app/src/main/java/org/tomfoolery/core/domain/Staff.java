package org.tomfoolery.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Staff extends ReadonlyUser {
    public Staff(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Timestamps timestamps) {
        super(credentials, audit, timestamps);
    }

    public static Staff of(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Timestamps timestamps) {
        return new Staff(credentials, audit, timestamps);
    }
}
