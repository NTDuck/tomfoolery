package org.tomfoolery.infrastructures.utils.helpers.comparators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class DocumentComparator {
    public static @NonNull Comparator<Document.Id> compareId() {
        return Comparator.comparing(Document.Id::getISBN_10);
    }

    public static @NonNull Comparator<Document> byIdAscending() {
        return Comparator.comparing(Document::getId, compareId());
    }

    public static @NonNull Comparator<Document> byCreationTimestampAscending() {
        return Comparator.comparing(
            document -> document.getAudit().getTimestamps().getCreated());
    }

    public static @NonNull Comparator<Document> byAverageRatingAscending() {
        return Comparator.comparing(document -> {
            val documentRating = document.getRating();

            if (documentRating == null)
                return Double.MAX_VALUE;

            return documentRating.getAverageRating();
        });
    }

    public static @NonNull Comparator<Document> byCreationTimestampDescending() {
        return byCreationTimestampAscending().reversed();
    }

    public static @NonNull Comparator<Document> byAverageRatingDescending() {
        return byAverageRatingAscending().reversed();
    }
}
