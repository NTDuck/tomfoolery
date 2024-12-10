package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.documents.CloudDocumentRepository;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudIndexedDocumentRecommendationGenerator implements DocumentRecommendationGenerator {
    private static final int NUMBER_OF_DOCUMENTS_PER_RECOMMENDATION = 10;

    private final @NonNull CloudDocumentRepository documentRepository;

    @Override
    public @NonNull List<Document> getLatestDocumentRecommendation() {
        return documentRepository.show().stream()
                .sorted(DocumentComparator.byCreationTimestampDescending()
                        .thenComparing(DocumentComparator.byIdAscending()))
                .limit(NUMBER_OF_DOCUMENTS_PER_RECOMMENDATION)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @NonNull List<Document> getTopRatedDocumentRecommendation() {
        return documentRepository.show().stream()
                .sorted(DocumentComparator.byAverageRatingDescending()
                        .thenComparing(DocumentComparator.byIdAscending()))
                .limit(NUMBER_OF_DOCUMENTS_PER_RECOMMENDATION)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Document savedDocument) {
        documentRepository.save(savedDocument);
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Document deletedDocument) {
        documentRepository.delete(deletedDocument.getId());
    }
}

