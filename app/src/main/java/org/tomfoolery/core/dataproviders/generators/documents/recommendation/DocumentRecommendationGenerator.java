package org.tomfoolery.core.dataproviders.generators.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.time.Instant;
import java.util.Collection;

public interface DocumentRecommendationGenerator {
    @NonNull Collection<FragmentaryDocument> generateLatestDocumentRecommendation();
    @NonNull Collection<FragmentaryDocument> generatePopularDocumentRecommendation();
    @NonNull Collection<FragmentaryDocument> generateTopRatedDocumentRecommendation();

    @NonNull Instant getLastGeneratedTimestamp();
    void setLastGeneratedTimestamp(@NonNull Instant lastGeneratedTimestamp);
}
