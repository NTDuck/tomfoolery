package org.tomfoolery.core.dataproviders.generators.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

import java.time.Instant;
import java.util.Collection;

public interface DocumentRecommendationGenerator {
    @NonNull Collection<Document> generateLatestDocumentRecommendation();
    @NonNull Collection<Document> generatePopularDocumentRecommendation();
    @NonNull Collection<Document> generateTopRatedDocumentRecommendation();

    @NonNull Instant getLastGeneratedTimestamp();
    void setLastGeneratedTimestamp(@NonNull Instant lastGeneratedTimestamp);
}
