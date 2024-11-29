package org.tomfoolery.core.domain.documents;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A view of a {@link Document}, with its content omitted.
 * Not to be confused with a {@link Document} with missing content.
 */
@Value(staticConstructor = "of")
public class DocumentWithoutContent {
    Document.@NonNull Id id;
    Document.@NonNull Audit audit;

    Document.@NonNull Metadata metadata;
    Document.@NonNull Rating rating;

    Document.@Nullable CoverImage coverImage;

    public static @NonNull DocumentWithoutContent of(@NonNull Document document) {
        return DocumentWithoutContent.of(document.getId(), document.getAudit(), document.getMetadata(), document.getRating(), document.getCoverImage());
    }

    public @NonNull Document withContent(Document.@Nullable Content content) {
        return Document.of(id, audit, metadata, rating, content, coverImage);
    }
}
