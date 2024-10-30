package org.tomfoolery.core.domain.documents;

import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

import java.time.Instant;
import java.time.Year;
import java.util.Collection;
import java.util.List;

@Data(staticConstructor = "of")
public final class Document implements ddd.Entity<Document.Id> {
    private final @NonNull Id id;

    private final @NonNull Content content;
    private final @NonNull Metadata metadata;
    private final @NonNull Audit audit;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.EntityId {
        @NonNull String ISBN;
    }

    @Data(staticConstructor = "of")
    public static class Content {
        private transient byte @NonNull [] bytes;
    }

    @Data(staticConstructor = "of")
    public static class Metadata {
        private @NonNull String title;
        private @NonNull String description;
        private @NonNull List<String> authors;
        private @NonNull List<String> genres;

        private @NonNull Year publishedYear;
        private @NonNull String publisher;

        private final @NonNull QRCode qrCode;

        @Data(staticConstructor = "of")
        public static class QRCode {
            private transient byte @NonNull [] bytes;
        }
    }

    @Data(staticConstructor = "of")
    public static class Audit {
        private final Staff.@NonNull Id createdByStaffId;
        private Staff.@Nullable Id lastModifiedByStaffId;

        private final @NonNull Collection<Patron.Id> borrowingPatronIds;

        private final @NonNull Timestamps timestamps;

        @Data(staticConstructor = "of")
        public static class Timestamps {
            private final @NonNull Instant created;
            private @Nullable Instant lastModified;
        }
    }
}
