package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class InMemoryDocumentRecommendationGenerator implements DocumentRecommendationGenerator {
    private static final int DOCUMENT_COUNT_PER_RECOMMENDATION = 10;

    private final @NonNull DocumentRepository documentRepository;

    @Getter(value = AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PROTECTED)
    private @NonNull Instant lastGeneratedTimestamp = Instant.EPOCH;

    public static @NonNull InMemoryDocumentRecommendationGenerator of(@NonNull DocumentRepository documentRepository) {
        return new InMemoryDocumentRecommendationGenerator(documentRepository);
    }

    private InMemoryDocumentRecommendationGenerator(@NonNull DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public @NonNull Collection<Document> generateLatestDocumentRecommendation() {
        val documentComparator = Comparator.<Document, Instant>comparing(document -> document.getAudit().getTimestamps().getCreated()).reversed();
        return generateDocumentRecommendationByComparator(documentComparator);
    }

    @Override
    public @NonNull Collection<Document> generatePopularDocumentRecommendation() {
        val documentComparator = Comparator.<Document, Integer>comparing(document -> document.getAudit().getBorrowingPatronIds().size()).reversed();
        return generateDocumentRecommendationByComparator(documentComparator);
    }

    @Override
    public @NonNull Collection<Document> generateTopRatedDocumentRecommendation() {
        val documentComparator = Comparator.<Document, Double>comparing(document -> document.getAudit().getRating().getRatingValue()).reversed();
        return generateDocumentRecommendationByComparator(documentComparator);
    }

    private @NonNull Collection<Document> generateDocumentRecommendationByComparator(@NonNull Comparator<Document> documentComparator) {
        val documents = this.documentRepository.show();

        return documents.stream()
            .sorted(documentComparator)
            .limit(DOCUMENT_COUNT_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }
}
