package org.tomfoolery.core.domain.auth.abc;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;

public class ModifiableUser extends BaseUser {
    protected ModifiableUser(@NonNull Id id, @NonNull Credentials credentials, @NonNull Audit audit) {
        super(id, credentials, audit);
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
    }

    @Getter @Setter
    public static class Audit extends BaseUser.Audit {
        public static @NonNull Audit of(@NonNull Timestamps timestamps) {
            return new Audit(timestamps);
        }

        protected Audit(@NonNull Timestamps timestamps) {
            super(timestamps);
        }

        @Override
        public @NonNull Timestamps getTimestamps() {
            return (Timestamps) super.getTimestamps();
        }

        @Getter @Setter
        public static class Timestamps extends BaseUser.Audit.Timestamps {
            private @Nullable Instant lastModified;

            public static @NonNull Timestamps of(@NonNull Instant created) {
                return new Timestamps(created);
            }

            protected Timestamps(@NonNull Instant created) {
                super(created);
            }
        }
    }
}
