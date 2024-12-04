package org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.aggregates.BaseBiRepositories;
import org.tomfoolery.core.dataproviders.aggregates.BaseSynchronizedRepository;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

public class SynchronizedDocumentRepository extends BaseSynchronizedRepository<Document, Document.Id> implements DocumentRepository {
    public static @NonNull SynchronizedDocumentRepository of(@NonNull DocumentRepository documentRepository, @NonNull List<BaseSynchronizedGenerator<Document, Document.Id>> synchronizedDocumentGenerators, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        return new SynchronizedDocumentRepository(documentRepository, synchronizedDocumentGenerators, documentContentRepository, borrowingSessionRepository, reviewRepository);
    }

    protected SynchronizedDocumentRepository(@NonNull DocumentRepository documentRepository, @NonNull List<BaseSynchronizedGenerator<Document, Document.Id>> synchronizedDocumentGenerators, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        super(
            documentRepository, synchronizedDocumentGenerators,
            List.of(documentContentRepository),
            BaseBiRepositories.of(List.of(borrowingSessionRepository, reviewRepository), List.of())
        );
    }
}
