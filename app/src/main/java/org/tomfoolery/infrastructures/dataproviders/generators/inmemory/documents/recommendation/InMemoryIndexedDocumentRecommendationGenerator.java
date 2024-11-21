package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc.BaseInMemorySynchronizedGenerator;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class InMemoryIndexedDocumentRecommendationGenerator extends BaseInMemorySynchronizedGenerator<Document, Document.Id> implements DocumentRecommendationGenerator {
    private static final int DOCUMENT_COUNT_PER_RECOMMENDATION = 10;

    private final @NonNull Set<FragmentaryDocument> fragmentaryDocumentsByCreationTimestamps = new TreeSet<>(
        DocumentComparator.byCreationTimestampDescending
            .thenComparing(DocumentComparator.byIdAscending)
    );

    private final @NonNull Set<FragmentaryDocument> fragmentaryDocumentsByNumberOfBorrowingPatrons = new TreeSet<>(
        DocumentComparator.byNumberOfBorrowingPatronsDescending
            .thenComparing(DocumentComparator.byIdAscending)
    );

    private final @NonNull Set<FragmentaryDocument> fragmentaryDocumentsByRatings = new TreeSet<>(
        DocumentComparator.byRatingDescending
            .thenComparing(DocumentComparator.byIdAscending)
    );

    @Override
    public @NonNull List<FragmentaryDocument> getLatestDocumentRecommendation() {
        return getDocumentRecommendation(this.fragmentaryDocumentsByCreationTimestamps);
    }

    @Override
    public @NonNull List<FragmentaryDocument> getPopularDocumentRecommendation() {
        return getDocumentRecommendation(this.fragmentaryDocumentsByNumberOfBorrowingPatrons);
    }

    @Override
    public @NonNull List<FragmentaryDocument> getTopRatedDocumentRecommendation() {
        return getDocumentRecommendation(this.fragmentaryDocumentsByRatings);
    }

    @Override
    public void synchronizeRecentlySavedEntities(@NonNull Set<Document> savedEntities) {
        savedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(this::synchronizeRecentlySavedEntity);
    }

    @Override
    public void synchronizeRecentlyDeletedEntities(@NonNull Set<Document> deletedEntities) {
        deletedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(this::synchronizeRecentlyDeletedEntity);
    }

    private static @NonNull List<FragmentaryDocument> getDocumentRecommendation(@NonNull Set<FragmentaryDocument> fragmentaryDocuments) {
        return fragmentaryDocuments.stream()   // parallelStream() does not guarantee order
            .limit(DOCUMENT_COUNT_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }

    private void synchronizeRecentlySavedEntity(@NonNull FragmentaryDocument fragmentaryDocument) {
        this.fragmentaryDocumentsByCreationTimestamps.add(fragmentaryDocument);
        this.fragmentaryDocumentsByNumberOfBorrowingPatrons.add(fragmentaryDocument);
        this.fragmentaryDocumentsByRatings.add(fragmentaryDocument);
    }

    private void synchronizeRecentlyDeletedEntity(@NonNull FragmentaryDocument fragmentaryDocument) {
        this.fragmentaryDocumentsByCreationTimestamps.remove(fragmentaryDocument);
        this.fragmentaryDocumentsByNumberOfBorrowingPatrons.remove(fragmentaryDocument);
        this.fragmentaryDocumentsByRatings.remove(fragmentaryDocument);
    }
}
