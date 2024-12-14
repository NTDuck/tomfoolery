package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation;

import lombok.Locked;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.abc.InMemoryLinearDocumentGenerator;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class InMemoryLinearDocumentRecommendationGenerator extends InMemoryLinearDocumentGenerator implements DocumentRecommendationGenerator {
    private static final int DOCUMENT_COUNT_PER_RECOMMENDATION = 10;

    @Override
    @Locked.Read
    public @NonNull List<Document> getLatestDocumentRecommendation() {
        return this.generateDocumentRecommendationByComparator(
            DocumentComparator.byCreationTimestampDescending()
                .thenComparing(DocumentComparator.byIdAscending())
        );
    }

    @Override
    @Locked.Read
    public @NonNull List<Document> getTopRatedDocumentRecommendation() {
        return this.generateDocumentRecommendationByComparator(
            DocumentComparator.byAverageRatingDescending()
                .thenComparing(DocumentComparator.byIdAscending())
        );
    }

    private @NonNull List<Document> generateDocumentRecommendationByComparator(@NonNull Comparator<Document> comparator) {
        return super.cachedEntities.parallelStream()
            .sorted(comparator)
            .limit(DOCUMENT_COUNT_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }
}
