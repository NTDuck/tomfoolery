package org.tomfoolery.core.domain.auth.abc;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.util.UUID;

@Data
public class BaseUser implements ddd.Entity<BaseUser.Id> {
    private final @NonNull Id id;

    private final @NonNull Credentials credentials;
    private final @NonNull Audit audit;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        @NonNull UUID value;
    }

    @Data(staticConstructor = "of")
    public static class Credentials {
        private @NonNull String username;
        private @NonNull String password;
    }

    @Getter @Setter
    public static class Audit {
        private boolean isLoggedIn;

        private final @NonNull Timestamps timestamps;

        public static @NonNull Audit of(boolean isLoggedIn, @NonNull Timestamps timestamps) {
            return new Audit(isLoggedIn, timestamps);
        }

        protected Audit(boolean isLoggedIn, @NonNull Timestamps timestamps) {
            this.isLoggedIn = isLoggedIn;
            this.timestamps = timestamps;
        }

        @Getter @Setter
        public static class Timestamps {
            private final @NonNull Instant created;
            private @Nullable Instant lastLogin;
            private @Nullable Instant lastLogout;

            public static @NonNull Timestamps of(@NonNull Instant created) {
                return new Timestamps(created);
            }

            protected Timestamps(@NonNull Instant created) {
                this.created = created;
            }
        }
    }
}
