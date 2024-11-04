package org.tomfoolery.infrastructures.dataproviders.inmemory.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.documents.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class InMemoryDocumentRecommendationGenerator implements DocumentRecommendationGenerator {
    private static int DAYS_BETWEEN_GENERATION = 1;
    private static int DOCUMENTS_PER_RECOMMENDATION = 10;

    private final @NonNull DocumentRepository documentRepository;
    private @NonNull Instant lastGenerated = Instant.EPOCH;

    private @NonNull Collection<Document> randomDocuments = List.of();
    private @NonNull Collection<Document> mostRecentDocuments = List.of();
    private @NonNull Collection<Document> highestRatingDocuments = List.of();
    private @NonNull Collection<Document> mostBorrowedDocuments = List.of();

    public static @NonNull InMemoryDocumentRecommendationGenerator of(@NonNull DocumentRepository documentRepository) {
        return new InMemoryDocumentRecommendationGenerator(documentRepository);
    }

    private InMemoryDocumentRecommendationGenerator(@NonNull DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        generateDocumentRecommendations();
    }

    @Override
    public @NonNull Collection<Document> getRandomDocuments() {
        if (isDocumentGenerationExpired())
            generateDocumentRecommendations();

        return this.randomDocuments;
    }

    @Override
    public @NonNull Collection<Document> getMostRecentDocuments() {
        if (isDocumentGenerationExpired())
            generateDocumentRecommendations();

        return this.mostRecentDocuments;
    }

    @Override
    public @NonNull Collection<Document> getHighestRatingDocuments() {
        if (isDocumentGenerationExpired())
            generateDocumentRecommendations();

        return this.highestRatingDocuments;
    }

    @Override
    public @NonNull Collection<Document> getMostBorrowedDocuments() {
        if (isDocumentGenerationExpired())
            generateDocumentRecommendations();

        return this.mostBorrowedDocuments;
    }

    private boolean isDocumentGenerationExpired() {
        return Instant.now()
            .minus(DAYS_BETWEEN_GENERATION, ChronoUnit.DAYS)
            .isAfter(this.lastGenerated);
    }

    private void generateDocumentRecommendations() {
        CompletableFuture.runAsync(() -> {
            val futureOfRandomDocuments = CompletableFuture.supplyAsync(this::generateRandomDocuments);
            val futureOfMostRecentDocuments = CompletableFuture.supplyAsync(this::generateMostRecentDocuments);
            val futureOfHighestRatingDocuments = CompletableFuture.supplyAsync(this::generateHighestRatingDocuments);
            val futureOfMostBorrowedDocuments = CompletableFuture.supplyAsync(this::generateMostBorrowedDocuments);

            CompletableFuture.allOf(
                futureOfRandomDocuments,
                futureOfMostRecentDocuments,
                futureOfHighestRatingDocuments,
                futureOfMostBorrowedDocuments
            ).thenRun(() -> {
                this.randomDocuments = futureOfRandomDocuments.join();
                this.mostRecentDocuments = futureOfMostRecentDocuments.join();
                this.highestRatingDocuments = futureOfHighestRatingDocuments.join();
                this.mostBorrowedDocuments = futureOfMostBorrowedDocuments.join();

                this.lastGenerated = Instant.now();
            });
        });
    }

    private @NonNull Collection<Document> generateRandomDocuments() {
        return List.of();
    }

    private @NonNull Collection<Document> generateMostRecentDocuments() {
        val documentComparator = Comparator.<Document, Instant>comparing(document -> document.getAudit().getTimestamps().getCreated()).reversed();
        return generateDocumentRecommendationByComparator(documentComparator);
    }

    private @NonNull Collection<Document> generateHighestRatingDocuments() {
        val documentComparator = Comparator.<Document, Double>comparing(document -> document.getAudit().getRating().getRatingValue()).reversed();
        return generateDocumentRecommendationByComparator(documentComparator);
    }

    private @NonNull Collection<Document> generateMostBorrowedDocuments() {
        val documentComparator = Comparator.<Document, Integer>comparing(document -> document.getAudit().getBorrowingPatronIds().size()).reversed();
        return generateDocumentRecommendationByComparator(documentComparator);
    }

    private @NonNull Collection<Document> generateDocumentRecommendationByComparator(@NonNull Comparator<Document> documentComparator) {
        return this.documentRepository.show().stream()
            .sorted(documentComparator)
            .limit(DOCUMENTS_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }
}