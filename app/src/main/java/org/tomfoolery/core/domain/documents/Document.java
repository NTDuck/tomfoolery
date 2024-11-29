package org.tomfoolery.core.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.contracts.ddd.ddd;
import org.tomfoolery.core.utils.helpers.adapters.documents.ISBNAdapter;
import org.tomfoolery.core.utils.helpers.verifiers.documents.ISBNVerifier;

import java.time.Instant;
import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data(staticConstructor = "of")
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public final class Document implements ddd.Entity<Document.Id> {
    private final @NonNull Id id;
    private final @NonNull Audit audit;

    private @NonNull Metadata metadata;
    private @NonNull Rating rating;

    private @Nullable Content content;
    private @Nullable CoverImage coverImage;

    @Value(staticConstructor = "ofISBN10")
    public static class Id implements ddd.EntityId {
        @NonNull String ISBN10;

        public static @Nullable Id of(@NonNull String ISBN) {
            if (ISBNVerifier.verifyISBN10(ISBN))
                return Id.ofISBN10(ISBN);

            if (ISBNVerifier.verifyISBN13(ISBN))
                return Id.ofISBN13(ISBN);

            return null;
        }

        public static @NonNull Id ofISBN13(@NonNull String ISBN13) {
            val ISBN10 = ISBNAdapter.toISBN10(ISBN13);
            return Id.ofISBN10(ISBN10);
        }

        public @NonNull String getISBN13() {
            return ISBNAdapter.toISBN13(ISBN10);
        }
    }

    @Data(staticConstructor = "of")
    public static class Audit {
        private final @NonNull Timestamps timestamps;

        private final Staff.@NonNull Id createdByStaffId;
        private Staff.@Nullable Id lastModifiedByStaffId;

        private final @NonNull Set<Patron.Id> borrowingPatronIds = Collections.synchronizedSet(new HashSet<>());

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

    @Data(staticConstructor = "of")
    public static class Rating {
        private static @Unsigned int MIN_RATING = 0;
        private static @Unsigned int MAX_RATING = 5;

        public void add(@Unsigned double rating) {
            verify(rating);

            this.averageRating = (this.averageRating * this.numberOfRatings + rating) / (++this.numberOfRatings);
        }

        public void remove(@Unsigned double rating) {
            if (this.numberOfRatings < 1)
                this.reset();
            else
                this.averageRating = (this.averageRating * this.numberOfRatings - rating) / (--this.numberOfRatings);
        }

        private static void verify(@Unsigned double rating) {
            if (rating < MIN_RATING || rating > MAX_RATING)
                throw new IllegalArgumentException(String.format("Invalid rating: '%s' (must be between %s and %s)", rating, MIN_RATING, MAX_RATING));
        }

        private void reset() {
            this.averageRating = MIN_RATING;
            this.numberOfRatings = 0;
        }

        private @Unsigned double averageRating;
        private @Unsigned int numberOfRatings = 0;
    }

    @Value(staticConstructor = "of")
    public static class Content {
        byte @NonNull [] bytes;
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
