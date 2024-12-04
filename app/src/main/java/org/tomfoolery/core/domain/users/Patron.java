package org.tomfoolery.core.domain.users;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.users.abc.ModifiableUser;

import java.time.LocalDate;

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

    @Data(staticConstructor = "of")
    public static class Metadata {
        private @NonNull Name name;

        private @NonNull LocalDate dateOfBirth;
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
