package org.tomfoolery.core.dataproviders.generators.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.time.Instant;
import java.util.List;

public interface DocumentRecommendationGenerator {
    @NonNull
    List<FragmentaryDocument> generateLatestDocumentRecommendation();
    @NonNull List<FragmentaryDocument> generatePopularDocumentRecommendation();
    @NonNull List<FragmentaryDocument> generateTopRatedDocumentRecommendation();

    @NonNull Instant getLastGeneratedTimestamp();
    void setLastGeneratedTimestamp(@NonNull Instant lastGeneratedTimestamp);
}
