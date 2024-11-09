package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.recommendation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.recommendation.DocumentRecommendationRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor(staticName = "of")
public class InMemoryDocumentRecommendationRepository implements DocumentRecommendationRepository {
    private @NonNull Collection<Document> latestDocumentRecommendation = List.of();
    private @NonNull Collection<Document> popularDocumentRecommendation = List.of();
    private @NonNull Collection<Document> topRatedDocumentRecommendation = List.of();

    @Override
    public void saveLatestDocumentRecommendation(@NonNull Collection<Document> latestDocumentRecommendation) {
        this.latestDocumentRecommendation = latestDocumentRecommendation;
    }

    @Override
    public void savePopularDocumentRecommendation(@NonNull Collection<Document> popularDocumentRecommendation) {
        this.popularDocumentRecommendation = popularDocumentRecommendation;
    }

    @Override
    public void saveTopRatedDocumentRecommendation(@NonNull Collection<Document> topRatedDocumentRecommendation) {
        this.topRatedDocumentRecommendation = topRatedDocumentRecommendation;
    }
}
