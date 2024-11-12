package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryDocumentRecommendationGenerator implements DocumentRecommendationGenerator {
    private static final int DOCUMENT_COUNT_PER_RECOMMENDATION = 10;

    private final @NonNull DocumentRepository documentRepository;

    @Getter @Setter
    private @NonNull Instant lastGeneratedTimestamp = Instant.EPOCH;

    public static @NonNull InMemoryDocumentRecommendationGenerator of(@NonNull DocumentRepository documentRepository) {
        return new InMemoryDocumentRecommendationGenerator(documentRepository);
    }

    private InMemoryDocumentRecommendationGenerator(@NonNull DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public @NonNull List<FragmentaryDocument> generateLatestDocumentRecommendation() {
        val fragmentaryDocumentComparator = Comparator.<FragmentaryDocument, Instant>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getTimestamps().getCreated()).reversed();
        return generateDocumentRecommendationByComparator(fragmentaryDocumentComparator);
    }

    @Override
    public @NonNull List<FragmentaryDocument> generatePopularDocumentRecommendation() {
        val fragmentaryDocumentComparator = Comparator.<FragmentaryDocument, Integer>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getBorrowingPatronIds().size()).reversed();
        return generateDocumentRecommendationByComparator(fragmentaryDocumentComparator);
    }

    @Override
    public @NonNull List<FragmentaryDocument> generateTopRatedDocumentRecommendation() {
        val fragmentaryDocumentComparator = Comparator.<FragmentaryDocument, Double>comparing(fragmentaryDocument -> fragmentaryDocument.getAudit().getRating().getValue()).reversed();
        return generateDocumentRecommendationByComparator(fragmentaryDocumentComparator);
    }

    private @NonNull List<FragmentaryDocument> generateDocumentRecommendationByComparator(@NonNull Comparator<FragmentaryDocument> fragmentaryDocumentComparator) {
        val fragmentaryDocuments = this.documentRepository.showFragments();

        return fragmentaryDocuments.parallelStream()
            .sorted(fragmentaryDocumentComparator)
            .limit(DOCUMENT_COUNT_PER_RECOMMENDATION)
            .collect(Collectors.toUnmodifiableList());
    }
}
