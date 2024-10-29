package org.tomfoolery.core.domain.auth.abc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

public class User extends ReadonlyUser {
    public User(@NonNull Credentials credentials, @NonNull Audit audit) {
        super(credentials, audit);
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Audit extends ReadonlyUser.Audit {
        protected Audit(@NonNull Timestamps timestamps) {
            super(timestamps);
        }

        public static @NonNull Audit of(@NonNull Timestamps timestamps) {
            return new Audit(timestamps);
        }

        @Override
        public @NonNull Timestamps getTimestamps() {
            return (Timestamps) super.getTimestamps();
        }

        @Data
        @EqualsAndHashCode(callSuper = true)
        @NoArgsConstructor(staticName = "of")
        public static class Timestamps extends ReadonlyUser.Audit.Timestamps {
            private @Nullable LocalDateTime lastModified = null;
        }
    }
}
