package org.tomfoolery.core.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

public class User extends ReadonlyUser {
    public User(@NonNull Credentials credentials) {
        super(credentials);
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Audit extends ReadonlyUser.Audit {
        public static @NonNull Audit of() {
            return new Audit();
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
