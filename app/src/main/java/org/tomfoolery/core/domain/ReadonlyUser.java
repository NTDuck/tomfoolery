package org.tomfoolery.core.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReadonlyUser {
    private final @NonNull ID id = ID.of();

    private final @NonNull Credentials credentials;
    private final @NonNull Audit audit;
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

    @Getter
    @AllArgsConstructor
    public static class Audit {
        private transient boolean isLoggedIn;
    }

    @Getter @Setter
    @RequiredArgsConstructor
    public static class Timestamps {
        private final @NonNull LocalDateTime created = LocalDateTime.now();
        private @NonNull LocalDateTime lastLogin;
        private @NonNull LocalDateTime lastLogout;
    }
}
