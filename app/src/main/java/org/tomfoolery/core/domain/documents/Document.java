package org.tomfoolery.core.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.helpers.adapters.documents.ISBNAdapter;
import org.tomfoolery.core.utils.helpers.verifiers.documents.ISBNVerifier;

import java.time.Instant;
import java.time.Year;
import java.util.List;

@Data(staticConstructor = "of")
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public final class Document implements ddd.Entity<Document.Id> {
    private final @NonNull Id id;
    private final @NonNull Audit audit;

    private @NonNull Metadata metadata;

    private @Nullable Rating rating;
    private @Nullable CoverImage coverImage;

    @Value(staticConstructor = "ofISBN10")
    public static class Id implements ddd.EntityId {
        @NonNull String ISBN_10;

        public static @Nullable Id of(@NonNull String ISBN) {
            if (ISBNVerifier.verifyISBN10(ISBN))
                return Id.ofISBN10(ISBN);

            if (ISBNVerifier.verifyISBN13(ISBN))
                return Id.ofISBN13(ISBN);

            return null;
        }

        public static @Nullable Id ofISBN13(@NonNull String ISBN_13) {
            val ISBN_10 = ISBNAdapter.toISBN10(ISBN_13);
            return Id.ofISBN10(ISBN_10);
        }
    }

    @Data(staticConstructor = "of")
    public static class Audit {
        private final @NonNull Timestamps timestamps;

        private final Staff.@NonNull Id createdByStaffId;
        private Staff.@Nullable Id lastModifiedByStaffId;

        @Data(staticConstructor = "of")
        public static class Timestamps {
            private final @NonNull Instant created;
            private @Nullable Instant lastModified;
        }
    }

    @Value(staticConstructor = "of")
    public static class Metadata {
        @NonNull String title;
        @NonNull String description;
        @NonNull List<String> authors;
        @NonNull List<String> genres;

        @NonNull Year publishedYear;
        @NonNull String publisher;
    }

    @Value(staticConstructor = "of")
    public static class Rating {
        @Unsigned double averageRating;
        @Unsigned int numberOfRatings;
    }

    @Value(staticConstructor = "of")
    public static class CoverImage {
        byte @NonNull [] bytes;
    }

    @Value(staticConstructor = "of")
    public static class QrCode {
        byte @NonNull [] bytes;
    }
}
