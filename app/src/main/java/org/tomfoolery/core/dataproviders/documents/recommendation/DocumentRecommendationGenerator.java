package org.tomfoolery.core.dataproviders.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

import java.util.Collection;

public interface DocumentRecommendationGenerator {
    @NonNull Collection<Document> getRandomDocuments();
    @NonNull Collection<Document> getMostRecentDocuments();
    @NonNull Collection<Document> getHighestRatingDocuments();
    @NonNull Collection<Document> getMostBorrowedDocuments();
}
