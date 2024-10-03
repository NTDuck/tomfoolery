package org.tomfoolery.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Staff extends User {
    public Staff(@NonNull Credentials credentials) {
        super(credentials);
    }

    public static Staff of(@NonNull Credentials credentials) {
        return new Staff(credentials);
    }
}
