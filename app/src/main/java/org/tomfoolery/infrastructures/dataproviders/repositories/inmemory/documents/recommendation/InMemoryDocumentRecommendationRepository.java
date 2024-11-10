package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.recommendation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.util.List;

@Getter
@NoArgsConstructor(staticName = "of")
public class InMemoryDocumentRecommendationRepository implements DocumentRecommendationRepository {
    private @NonNull List<FragmentaryDocument> latestDocumentRecommendation = List.of();
    private @NonNull List<FragmentaryDocument> popularDocumentRecommendation = List.of();
    private @NonNull List<FragmentaryDocument> topRatedDocumentRecommendation = List.of();

    @Override
    public void saveLatestDocumentRecommendation(@NonNull List<FragmentaryDocument> latestDocumentRecommendation) {
        this.latestDocumentRecommendation = latestDocumentRecommendation;
    }

    @Override
    public void savePopularDocumentRecommendation(@NonNull List<FragmentaryDocument> popularDocumentRecommendation) {
        this.popularDocumentRecommendation = popularDocumentRecommendation;
    }

    @Override
    public void saveTopRatedDocumentRecommendation(@NonNull List<FragmentaryDocument> topRatedDocumentRecommendation) {
        this.topRatedDocumentRecommendation = topRatedDocumentRecommendation;
    }
}
