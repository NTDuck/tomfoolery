package org.tomfoolery.core.domain.auth.abc;

import lombok.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.utils.contracts.ddd.ddd;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

import java.time.Instant;
import java.util.UUID;

@Data
public class BaseUser implements ddd.Entity<BaseUser.Id> {
    private final @NonNull Id id;

    private @NonNull Credentials credentials;
    private final @NonNull Audit audit;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        @NonNull UUID value;
    }

    @Data(staticConstructor = "of")
    public static class Credentials implements ddd.ValueObject{
        private @NonNull String username;
        @With private @NonNull SecureString password;
    }

    @Getter @Setter
    public static class Audit implements ddd.ValueObject {
        private final @NonNull Timestamps timestamps;

        public static @NonNull Audit of(@NonNull Timestamps timestamps) {
            return new Audit(timestamps);
        }

        protected Audit(@NonNull Timestamps timestamps) {
            this.timestamps = timestamps;
        }

        @Getter @Setter
        public static class Timestamps implements ddd.ValueObject {
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
