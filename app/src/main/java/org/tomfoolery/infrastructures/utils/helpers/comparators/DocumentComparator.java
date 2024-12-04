package org.tomfoolery.infrastructures.utils.helpers.comparators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class DocumentComparator {
    public static final @NonNull Comparator<Document> byIdAscending = Comparator.comparing(
        document -> document.getId().getISBN_10()
    );

    public static final @NonNull Comparator<Document> byCreationTimestampAscending = Comparator.comparing(
        document -> document.getAudit().getTimestamps().getCreated()
    );

    public static final @NonNull Comparator<Document> byAverageRatingAscending = Comparator.comparing(document -> {
        val documentRating = document.getRating();

        if (documentRating == null)
            return Double.MAX_VALUE;

        return documentRating.getAverageRating();
    });

    public static final @NonNull Comparator<Document> byCreationTimestampDescending = byCreationTimestampAscending.reversed();
    public static final @NonNull Comparator<Document> byAverageRatingDescending = byAverageRatingAscending.reversed();
}
