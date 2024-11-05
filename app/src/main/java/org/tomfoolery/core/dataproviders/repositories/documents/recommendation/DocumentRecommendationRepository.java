package org.tomfoolery.core.dataproviders.repositories.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

import java.util.Collection;

public interface DocumentRecommendationRepository {
    void saveLatestDocumentRecommendation(@NonNull Collection<Document> latestDocumentRecommendation);
    void savePopularDocumentRecommendation(@NonNull Collection<Document> popularDocumentRecommendation);
    void saveTopRatedDocumentRecommendation(@NonNull Collection<Document> topRatedDocumentRecommendation);

    @NonNull Collection<Document> getLatestDocumentRecommendation();
    @NonNull Collection<Document> getPopularDocumentRecommendation();
    @NonNull Collection<Document> getTopRatedDocumentRecommendation();
}
