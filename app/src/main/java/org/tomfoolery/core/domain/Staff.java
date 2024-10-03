package org.tomfoolery.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Staff extends User {
    public Staff(@NonNull Credentials credentials, @NonNull Timestamps timestamps) {
        super(credentials, timestamps);
    }

    public static Staff of(@NonNull Credentials credentials, @NonNull Timestamps timestamps) {
        return new Staff(credentials, timestamps);
    }
}
