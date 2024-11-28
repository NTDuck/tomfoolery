package org.tomfoolery.core.domain.documents;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data(staticConstructor = "of")
public final class Document implements ddd.Entity<Document.Id> {
    public static byte @NonNull [] EMPTY_BYTES = new byte[0];

    private final @NonNull Id id;
    private final @NonNull Audit audit;

    private @NonNull Metadata metadata;
    private @NonNull Rating rating;

    private @NonNull Content content;
    private @NonNull CoverImage coverImage;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        @NonNull String ISBN;
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
