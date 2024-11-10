package org.tomfoolery.core.dataproviders.repositories.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.util.List;

public interface DocumentRecommendationRepository {
    void saveLatestDocumentRecommendation(@NonNull List<FragmentaryDocument> latestDocumentRecommendation);
    void savePopularDocumentRecommendation(@NonNull List<FragmentaryDocument> popularDocumentRecommendation);
    void saveTopRatedDocumentRecommendation(@NonNull List<FragmentaryDocument> topRatedDocumentRecommendation);

    @NonNull List<FragmentaryDocument> getLatestDocumentRecommendation();
    @NonNull List<FragmentaryDocument> getPopularDocumentRecommendation();
    @NonNull List<FragmentaryDocument> getTopRatedDocumentRecommendation();
}
