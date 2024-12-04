package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class InMemoryIndexedDocumentRecommendationGenerator implements DocumentRecommendationGenerator {
    private static final int NUMBER_OF_DOCUMENTS_PER_RECOMMENDATION = 10;

    private final @NonNull Set<Document> documentsByCreationTimestamps = new TreeSet<>(
        DocumentComparator.byCreationTimestampDescending
            .thenComparing(DocumentComparator.byIdAscending)
    );

    private final @NonNull Set<Document> documentsByAverageRatings = new TreeSet<>(
        DocumentComparator.byAverageRatingDescending
            .thenComparing(DocumentComparator.byIdAscending)
    );

    @Override
    public @NonNull List<Document> getLatestDocumentRecommendation() {
        return getDocumentRecommendation(this.documentsByCreationTimestamps);
    }

    @Override
    public @NonNull List<Document> getTopRatedDocumentRecommendation() {
        return getDocumentRecommendation(this.documentsByAverageRatings);
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Document savedDocument) {
        this.documentsByCreationTimestamps.add(savedDocument);
        this.documentsByAverageRatings.add(savedDocument);
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Document deletedDocument) {
        this.documentsByCreationTimestamps.remove(deletedDocument);
        this.documentsByAverageRatings.remove(deletedDocument);
    }

    private static @NonNull List<Document> getDocumentRecommendation(@NonNull Set<Document> documents) {
        return documents.stream()   // parallelStream() does not guarantee order
            .limit(NUMBER_OF_DOCUMENTS_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }
}
