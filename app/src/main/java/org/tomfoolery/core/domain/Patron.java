package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.abc.User;

import java.util.Collection;
import java.util.HashSet;

@Getter
public class Patron extends User {
    private final @NonNull Metadata metadata;

    public Patron(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Metadata metadata) {
        super(credentials, audit);
        this.metadata = metadata;
    }

    public static @NonNull Patron of(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Metadata metadata) {
        return new Patron(credentials, audit, metadata);
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
    }

    @Data(staticConstructor = "of")
    public static class Metadata {
        private @NonNull String fullName;
        private @NonNull String address;
        private @NonNull String gmail;
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Audit extends User.Audit {
        private final @NonNull Collection<Document.Id> borrowedDocumentIds = new HashSet<>();

        private Audit(@NonNull Timestamps timestamps) {
            super(timestamps);
        }

        public static @NonNull Audit of(@NonNull Timestamps timestamps) {
            return new Audit(timestamps);
        }
    }
}
