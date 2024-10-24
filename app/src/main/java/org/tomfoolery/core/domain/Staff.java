package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.domain.abc.User;

public class Staff extends ReadonlyUser {
    public Staff(@NonNull Credentials credentials, @NonNull Audit audit) {
        super(credentials, audit);
    }

    public static @NonNull Staff of(@NonNull Credentials credentials, @NonNull Audit audit) {
        return new Staff(credentials, audit);
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Audit extends User.Audit {
        private final Administrator.@NonNull Id createdByAdminId;
        private Administrator.@Nullable Id lastModifiedByAdminId = null;

        protected Audit(Administrator.@NonNull Id createdByAdminId, @NonNull Timestamps timestamps) {
            super(timestamps);
            this.createdByAdminId = createdByAdminId;
        }

        public static @NonNull Audit of(Administrator.@NonNull Id createdByAdminId, @NonNull Timestamps timestamps) {
            return new Audit(createdByAdminId, timestamps);
        }
    }
}
