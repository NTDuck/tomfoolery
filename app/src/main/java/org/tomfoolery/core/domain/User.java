package org.tomfoolery.core.domain;

import lombok.NonNull;

import java.time.LocalDateTime;

public class User extends ReadonlyUser {
    protected @NonNull LocalDateTime lastModifiedTimestamp;
}
