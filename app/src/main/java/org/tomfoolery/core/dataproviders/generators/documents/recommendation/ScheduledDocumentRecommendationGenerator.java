package org.tomfoolery.core.dataproviders.generators.documents.recommendation;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class ScheduledDocumentRecommendationGenerator implements DocumentRecommendationGenerator {
    private static final @NonNull Duration GENERATION_INTERVAL = Duration.ofDays(1);
    private static final int DOCUMENTS_PER_RECOMMENDATION = 10;

    protected final @NonNull DocumentRepository documentRepository;

    protected ScheduledDocumentRecommendationGenerator(@NonNull DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public final @NonNull Collection<Document> getRandomDocuments() {
        if (isGenerationIntervalElapsed())
            generateDocumentRecommendations();

        return this.generateRandomDocuments();
    }

    @Override
    public final @NonNull Collection<Document> getMostRecentDocuments() {
        if (isGenerationIntervalElapsed())
            generateDocumentRecommendations();

        return this.getGeneratedMostRecentDocuments();
    }

    @Override
    public final @NonNull Collection<Document> getHighestRatingDocuments() {
        if (isGenerationIntervalElapsed())
            generateDocumentRecommendations();

        return this.getGeneratedHighestRatingDocuments();
    }

    @Override
    public final @NonNull Collection<Document> getMostBorrowedDocuments() {
        if (isGenerationIntervalElapsed())
            generateDocumentRecommendations();

        return this.getGeneratedMostBorrowedDocuments();
    }

    private boolean isGenerationIntervalElapsed() {
        val lastGenerated = getLastGeneratedTimestamp();

        return Duration.between(lastGenerated, Instant.now())
            .compareTo(GENERATION_INTERVAL) > 0;
    }

    private void generateDocumentRecommendations() {
        CompletableFuture.runAsync(() -> {
            val futureOfMostRecentDocuments = CompletableFuture.supplyAsync(this::generateMostRecentDocuments);
            val futureOfHighestRatingDocuments = CompletableFuture.supplyAsync(this::generateHighestRatingDocuments);
            val futureOfMostBorrowedDocuments = CompletableFuture.supplyAsync(this::generateMostBorrowedDocuments);

            CompletableFuture.allOf(
                futureOfMostRecentDocuments,
                futureOfHighestRatingDocuments,
                futureOfMostBorrowedDocuments
            ).thenRun(() -> {
                setGeneratedMostRecentDocuments(futureOfMostRecentDocuments.join());
                setGeneratedHighestRatingDocuments(futureOfHighestRatingDocuments.join());
                setGeneratedMostBorrowedDocuments(futureOfMostBorrowedDocuments.join());

                setLastGeneratedTimestamp(Instant.now());
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

    protected abstract @NonNull Collection<Document> getGeneratedMostRecentDocuments();
    protected abstract @NonNull Collection<Document> getGeneratedHighestRatingDocuments();
    protected abstract @NonNull Collection<Document> getGeneratedMostBorrowedDocuments();

    protected abstract void setGeneratedMostRecentDocuments(@NonNull Collection<Document> documents);
    protected abstract void setGeneratedHighestRatingDocuments(@NonNull Collection<Document> documents);
    protected abstract void setGeneratedMostBorrowedDocuments(@NonNull Collection<Document> documents);

    protected abstract @NonNull Instant getLastGeneratedTimestamp();
    protected abstract void setLastGeneratedTimestamp(@NonNull Instant timestamp);
}
