package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data(staticConstructor = "of")
public final class Document {
    private final @NonNull Id id;

    private @NonNull Metadata metadata;
    private final @NonNull Audit audit;

    @Value(staticConstructor = "of")
    public static class Id {
        @NonNull String value;
    }

    @Data(staticConstructor = "of")
    public static class Metadata {
        private @NonNull String title;
        private @NonNull String description;
        private @NonNull List<String> authors = new ArrayList<>();
        private @NonNull List<String> genres = new ArrayList<>();
        private transient @NonNull BufferedImage qrCode;
    }

    @Data(staticConstructor = "of")
    public static class Audit {
        private final ReadonlyUser.@NonNull Id createdByStaffId;
        private @Nullable ReadonlyUser.Id lastModifiedByStaffId = null;

        private final @NonNull Collection<ReadonlyUser.Id> borrowingPatronIds = new HashSet<>();

        private final @NonNull Timestamps timestamps = Timestamps.of();

        @Data(staticConstructor = "of")
        public static class Timestamps {
            private final @NonNull LocalDateTime created = LocalDateTime.now();
            private @Nullable LocalDateTime lastModified = null;
        }
    }
}
