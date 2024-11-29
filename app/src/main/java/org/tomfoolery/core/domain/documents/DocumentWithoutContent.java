package org.tomfoolery.core.domain.documents;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Value(staticConstructor = "of")
public class DocumentWithoutContent {
    Document.@NonNull Id id;
    Document.@NonNull Audit audit;

    Document.@NonNull Metadata metadata;
    Document.@NonNull Rating rating;

    Document.@Nullable CoverImage coverImage;

    public @NonNull Document withContent(Document.@Nullable Content content) {
        return Document.of(id, audit, metadata, rating, content, coverImage);
    }
}
