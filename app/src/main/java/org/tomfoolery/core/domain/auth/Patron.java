package org.tomfoolery.core.domain.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.auth.abc.ModifiableUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter @Setter
public final class Patron extends ModifiableUser {
    private @NonNull Metadata metadata;

    public static @NonNull Patron of(@NonNull Id id, @NonNull Credentials credentials, @NonNull Audit audit, @NonNull Metadata metadata) {
        return new Patron(id, credentials, audit, metadata);
    }

    private Patron(@NonNull Id id, @NonNull Credentials credentials, @NonNull Audit audit, @NonNull Metadata metadata) {
        super(id, credentials, audit);
        this.metadata = metadata;
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
    }

    @Data(staticConstructor = "of")
    public static class Metadata implements ddd.ValueObject {
        private @NonNull String fullName;
        private @NonNull String address;
        private @NonNull String email;
    }

    @Getter @Setter
    public static class Audit extends ModifiableUser.Audit {
        private final @NonNull Set<Document.Id> borrowedDocumentIds = Collections.synchronizedSet(new HashSet<>());
        private final @NonNull Map<Document.Id, Double> ratingsByDocumentIds = Collections.synchronizedMap(new HashMap<>());

        public static @NonNull Audit of(@NonNull Timestamps timestamps) {
            return new Audit(timestamps);
        }

        protected Audit(@NonNull Timestamps timestamps) {
            super(timestamps);
        }
    }
}
