package org.tomfoolery.core.domain;

import lombok.*;

import java.util.Collection;
import java.util.HashSet;

@Getter @Setter
public class Patron extends User {
    private final @NonNull Metadata metadata;

    public Patron(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Timestamps timestamps, @NonNull Metadata metadata) {
        super(credentials, audit, timestamps);
        this.metadata = metadata;
    }

    public static Patron of(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Timestamps timestamps, @NonNull Metadata metadata) {
        return new Patron(credentials, audit, timestamps, metadata);
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

    public static class Audit extends User.Audit {
        private final @NonNull Collection<Document.ID> borrowedDocumentIds = new HashSet<>();

        public Audit(boolean isLoggedIn) {
            super(isLoggedIn);
        }
    }
}
