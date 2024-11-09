package org.tomfoolery.core.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.contracts.ddd.ddd;
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;

import java.time.Instant;
import java.time.Year;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data(staticConstructor = "of")
public final class Document implements ddd.Entity<Document.Id> {
    private final @NonNull Id id;

    private @NonNull Content content;
    private @NonNull Metadata metadata;
    private @NonNull Audit audit;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        @NonNull String ISBN;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class Content implements ddd.ValueObject {
        private byte @NonNull [] bytes;
    }

    @Data(staticConstructor = "of")
    public static class Metadata implements ddd.ValueObject {
        private @NonNull String title;
        private @NonNull String description;
        private @NonNull List<String> authors;
        private @NonNull List<String> genres;

        private @NonNull Year publishedYear;
        private @NonNull String publisher;

        private @NonNull CoverImage coverImage;

        @Data
        @AllArgsConstructor(staticName = "of")
        public static class CoverImage implements ddd.ValueObject {
            private byte @NonNull [] bytes;
        }
    }

    @Data(staticConstructor = "of")
    public static class Audit implements ddd.ValueObject {
        private final Staff.@NonNull Id createdByStaffId;
        private Staff.@Nullable Id lastModifiedByStaffId;

        private final @NonNull Collection<Patron.Id> borrowingPatronIds = new HashSet<>();
        private final @NonNull AverageRating rating;

        private final @NonNull Timestamps timestamps;

        @Data(staticConstructor = "of")
        public static class Timestamps implements ddd.ValueObject {
            private final @NonNull Instant created;
            private @Nullable Instant lastModified;
        }
    }

    @Value(staticConstructor = "of")
    public static class QrCode implements ddd.ValueObject {
        byte @NonNull [] bytes;
    }
}
