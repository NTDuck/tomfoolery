package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.abc.InMemoryLinearDocumentGenerator;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class InMemoryLinearDocumentRecommendationGenerator extends InMemoryLinearDocumentGenerator implements DocumentRecommendationGenerator {
    private static final int DOCUMENT_COUNT_PER_RECOMMENDATION = 10;

    @Override
    public @NonNull List<FragmentaryDocument> getLatestDocumentRecommendation() {
        val fragmentaryDocumentComparator = Comparator.<FragmentaryDocument, Instant>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getTimestamps().getCreated()).reversed();
        return generateDocumentRecommendationByComparator(fragmentaryDocumentComparator);
    }

    @Override
    public @NonNull List<FragmentaryDocument> getPopularDocumentRecommendation() {
        val fragmentaryDocumentComparator = Comparator.<FragmentaryDocument, Integer>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getBorrowingPatronIds().size()).reversed();
        return generateDocumentRecommendationByComparator(fragmentaryDocumentComparator);
    }

    @Override
    public @NonNull List<FragmentaryDocument> getTopRatedDocumentRecommendation() {
        val fragmentaryDocumentComparator = Comparator.<FragmentaryDocument, Double>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getRating().getValue()).reversed();
        return generateDocumentRecommendationByComparator(fragmentaryDocumentComparator);
    }

    private @NonNull List<FragmentaryDocument> generateDocumentRecommendationByComparator(@NonNull Comparator<FragmentaryDocument> fragmentaryDocumentComparator) {
        return super.fragmentaryDocuments.parallelStream()
            .sorted(fragmentaryDocumentComparator)
            .limit(DOCUMENT_COUNT_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }
}
