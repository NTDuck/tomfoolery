package org.tomfoolery.core.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReadonlyUser {
    private final @NonNull Id id = Id.of();

    private final @NonNull Credentials credentials;
    private final @NonNull Audit audit = Audit.of();

    @Value(staticConstructor = "of")
    public static class Id {
        @NonNull UUID value = UUID.randomUUID();
    }

    @Data(staticConstructor = "of")
    public static class Credentials {
        private @NonNull String username;
        private @NonNull String password;
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Audit {
        private transient boolean isLoggedIn = false;

        private final @NonNull Timestamps timestamps = Timestamps.of();

        public static @NonNull Audit of() {
            return new Audit();
        }

        @Data
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Timestamps {
            private final @NonNull LocalDateTime created = LocalDateTime.now();
            private @Nullable LocalDateTime lastLogin = null;
            private @Nullable LocalDateTime lastLogout = null;

            public static @NonNull Timestamps of() {
                return new Timestamps();
            }
        }
    }
}
