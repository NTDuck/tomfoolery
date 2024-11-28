package org.tomfoolery.core.domain.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.auth.abc.ModifiableUser;
import org.tomfoolery.core.domain.documents.Document;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter @Setter
public final class Patron extends ModifiableUser {
    private @NonNull Metadata metadata;

    public static @NonNull Patron of(@NonNull Id id, @NonNull Audit audit, @NonNull Credentials credentials, @NonNull Metadata metadata) {
        return new Patron(id, audit, credentials, metadata);
    }

    private Patron(@NonNull Id id, @NonNull Audit audit, @NonNull Credentials credentials, @NonNull Metadata metadata) {
        super(id, audit, credentials);
        this.metadata = metadata;
    }

    @Override
    public @NonNull Audit getAudit() {
        return (Audit) super.getAudit();
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

    @Data(staticConstructor = "of")
    public static class Metadata {
        private @NonNull Name name;

        private @NonNull Date dateOfBirth;
        private @NonNull String phoneNumber;

        private @NonNull Address address;
        private @NonNull String email;

        @Value(staticConstructor = "of")
        public static class Name {
            @NonNull String firstName;
            @NonNull String lastName;
        }

        @Value(staticConstructor = "of")
        public static class Address {
            @NonNull String city;
            @NonNull String country;
        }
    }
}
