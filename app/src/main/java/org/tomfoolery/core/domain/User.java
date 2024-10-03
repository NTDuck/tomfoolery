package org.tomfoolery.core.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private final @NonNull ID id = ID.of();

    private final @NonNull Credentials credentials;
    private final @NonNull Timestamps timestamps;

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
        private @NonNull LocalDateTime lastLogin;
        private @NonNull LocalDateTime lastLogout;
    }
}
