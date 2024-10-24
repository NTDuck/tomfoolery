package org.tomfoolery.core.domain.abc;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.contracts.ddd;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReadonlyUser implements ddd.Entity<ReadonlyUser.Id> {
    private final @NonNull Id id = Id.of();

    private final @NonNull Credentials credentials;
    private final @NonNull Audit audit;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        @NonNull UUID value = UUID.randomUUID();
    }

    @Data(staticConstructor = "of")
    public static class Credentials {
        private @NonNull String username;
        private @NonNull String password;
    }

    @Data
    public static class Audit {
        private boolean isLoggedIn = false;
        private final @NonNull Timestamps timestamps;

        protected Audit(@NonNull Timestamps timestamps) {
            this.timestamps = timestamps;
        }

        public static @NonNull Audit of(@NonNull Timestamps timestamps) {
            return new Audit(timestamps);
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
