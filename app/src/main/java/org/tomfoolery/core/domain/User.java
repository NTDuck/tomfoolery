package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class User {
    private final @NonNull ID id = ID.of();

    private final @NonNull Credentials credentials;
    private final @NonNull Timestamps timestamps = Timestamps.of();

    public User(@NonNull Credentials credentials) {
        this.credentials = credentials;
    }

    @Value(staticConstructor = "of")
    public static class ID {
        @NonNull UUID value = UUID.randomUUID();
    }

    @Data(staticConstructor = "of")
    public static class Credentials {
        private @NonNull String username;
        private @NonNull String password;
    }

    @Data(staticConstructor = "of")
    public static class Timestamps {
        private final @NonNull LocalDateTime created = LocalDateTime.now();
        private @NonNull LocalDateTime lastModified = LocalDateTime.now();
    }
}
