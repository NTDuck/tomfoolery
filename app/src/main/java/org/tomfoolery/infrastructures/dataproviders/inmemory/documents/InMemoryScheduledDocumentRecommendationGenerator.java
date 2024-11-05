package org.tomfoolery.infrastructures.dataproviders.inmemory.documents;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.ScheduledDocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter(value = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PROTECTED)
public class InMemoryScheduledDocumentRecommendationGenerator extends ScheduledDocumentRecommendationGenerator {
    private @NonNull Instant lastGeneratedTimestamp = Instant.now();

    private @NonNull Collection<Document> generatedMostRecentDocuments = List.of();
    private @NonNull Collection<Document> generatedHighestRatingDocuments = List.of();
    private @NonNull Collection<Document> generatedMostBorrowedDocuments = List.of();

    public static @NonNull InMemoryScheduledDocumentRecommendationGenerator of(@NonNull DocumentRepository documentRepository) {
        return new InMemoryScheduledDocumentRecommendationGenerator(documentRepository);
    }

    protected InMemoryScheduledDocumentRecommendationGenerator(@NonNull DocumentRepository documentRepository) {
        super(documentRepository);
    }
}
