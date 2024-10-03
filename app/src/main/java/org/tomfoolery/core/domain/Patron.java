package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
public class Patron extends User {
    private final @NonNull Metadata metadata;
    private final @NonNull Audit audit = Audit.of();

    public Patron(@NonNull Credentials credentials, @NonNull Timestamps timestamps, @NonNull Metadata metadata) {
        super(credentials, timestamps);
        this.metadata = metadata;
    }

    public static Patron of(@NonNull Credentials credentials, @NonNull Timestamps timestamps, @NonNull Metadata metadata) {
        return new Patron(credentials, timestamps, metadata);
    }

    @Data(staticConstructor = "of")
    public static class Metadata {
        private @NonNull String fullName;
        private @NonNull String address;
        private @NonNull String gmail;
    }

    @Data(staticConstructor = "of")
    public static class Audit {
        private final @NonNull Collection<Document.ID> borrowedDocumentIds = new HashSet<>();
    }
}
