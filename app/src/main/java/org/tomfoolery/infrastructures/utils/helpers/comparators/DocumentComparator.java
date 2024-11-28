package org.tomfoolery.infrastructures.utils.helpers.comparators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class DocumentComparator {
    public static final @NonNull Comparator<FragmentaryDocument> byIdAscending = Comparator.comparing(fragmentaryDocument -> fragmentaryDocument.getId().getISBN());
    public static final @NonNull Comparator<FragmentaryDocument> byCreationTimestampAscending = Comparator.<FragmentaryDocument, Instant>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getTimestamps().getCreated());
    public static final @NonNull Comparator<FragmentaryDocument> byNumberOfBorrowingPatronsAscending = Comparator.<FragmentaryDocument, Integer>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getBorrowingPatronIds().size());
    public static final @NonNull Comparator<FragmentaryDocument> byRatingAscending = Comparator.<FragmentaryDocument, Double>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getRating().getValue());

    public static final @NonNull Comparator<FragmentaryDocument> byCreationTimestampDescending = byCreationTimestampAscending.reversed();
    public static final @NonNull Comparator<FragmentaryDocument> byNumberOfBorrowingPatronsDescending = byNumberOfBorrowingPatronsAscending.reversed();
    public static final @NonNull Comparator<FragmentaryDocument> byRatingDescending = byRatingAscending.reversed();
}
