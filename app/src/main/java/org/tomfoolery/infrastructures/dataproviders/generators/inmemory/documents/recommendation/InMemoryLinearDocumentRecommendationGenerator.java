package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.abc.InMemoryLinearDocumentGenerator;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class InMemoryLinearDocumentRecommendationGenerator extends InMemoryLinearDocumentGenerator implements DocumentRecommendationGenerator {
    private static final int DOCUMENT_COUNT_PER_RECOMMENDATION = 10;

    @Override
    public @NonNull List<FragmentaryDocument> getLatestDocumentRecommendation() {
        return generateDocumentRecommendationByComparator(
            DocumentComparator.byCreationTimestampDescending
                .thenComparing(DocumentComparator.byIdAscending)
        );
    }

    @Override
    public @NonNull List<FragmentaryDocument> getPopularDocumentRecommendation() {
        return generateDocumentRecommendationByComparator(
            DocumentComparator.byNumberOfBorrowingPatronsDescending
                .thenComparing(DocumentComparator.byIdAscending)
        );
    }

    @Override
    public @NonNull List<FragmentaryDocument> getTopRatedDocumentRecommendation() {
        return generateDocumentRecommendationByComparator(
            DocumentComparator.byRatingDescending
                .thenComparing(DocumentComparator.byIdAscending)
        );
    }

    private @NonNull List<FragmentaryDocument> generateDocumentRecommendationByComparator(@NonNull Comparator<FragmentaryDocument> comparator) {
        return super.fragmentaryDocuments.parallelStream()
            .sorted(comparator)
            .limit(DOCUMENT_COUNT_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }
}
