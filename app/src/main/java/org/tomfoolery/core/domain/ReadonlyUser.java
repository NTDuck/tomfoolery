package org.tomfoolery.core.domain;

import lombok.NonNull;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReadonlyUser {
    private final @NonNull ID id = ID.of();

    private @NonNull String name;
    private @NonNull String password;   // Hashed

    protected final @NonNull LocalDateTime createdTimestamp = LocalDateTime.now();
    protected @NonNull LocalDateTime lastLoginTimestamp;
    protected @NonNull LocalDateTime lastLogoutTimestamp;

    @Value(staticConstructor = "of")
    public static class ID {
        @NonNull UUID value = UUID.randomUUID();
    }
}
