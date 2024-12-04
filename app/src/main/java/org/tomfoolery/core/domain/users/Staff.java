package org.tomfoolery.core.domain.users;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.users.abc.ModifiableUser;

public final class Staff extends ModifiableUser {
    public static @NonNull Staff of(@NonNull Id id, @NonNull Audit audit, @NonNull Credentials credentials) {
        return new Staff(id, audit, credentials);
    }

    private Staff(@NonNull Id id, @NonNull Audit audit, @NonNull Credentials credentials) {
        super(id, audit, credentials);
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
    }

    @Getter @Setter
    public static class Audit extends ModifiableUser.Audit {
        private final Administrator.@NonNull Id createdByAdminId;
        private Administrator.@Nullable Id lastModifiedByAdminId;

        public static @NonNull Audit of(@NonNull Timestamps timestamps, Administrator.@NonNull Id createdByAdminId) {
            return new Audit(timestamps, createdByAdminId);
        }

        protected Audit(@NonNull Timestamps timestamps, Administrator.@NonNull Id createdByAdminId) {
            super(timestamps);

            this.createdByAdminId = createdByAdminId;
        }
    }
}
