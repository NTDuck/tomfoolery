package org.tomfoolery.core.dataproviders.repositories.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.util.Collection;

public interface DocumentRecommendationRepository {
    void saveLatestDocumentRecommendation(@NonNull Collection<FragmentaryDocument> latestDocumentRecommendation);
    void savePopularDocumentRecommendation(@NonNull Collection<FragmentaryDocument> popularDocumentRecommendation);
    void saveTopRatedDocumentRecommendation(@NonNull Collection<FragmentaryDocument> topRatedDocumentRecommendation);

    @NonNull Collection<FragmentaryDocument> getLatestDocumentRecommendation();
    @NonNull Collection<FragmentaryDocument> getPopularDocumentRecommendation();
    @NonNull Collection<FragmentaryDocument> getTopRatedDocumentRecommendation();
}
