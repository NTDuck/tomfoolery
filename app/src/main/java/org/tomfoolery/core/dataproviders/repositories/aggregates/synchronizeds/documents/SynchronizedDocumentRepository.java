package org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.containers.relations.BiRelationRepositories;
import org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.abc.BaseSynchronizedRepository;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.containers.relations.UniRelationRepositories;

import java.util.List;

public class SynchronizedDocumentRepository extends BaseSynchronizedRepository<Document, Document.Id> implements DocumentRepository {
    public static @NonNull SynchronizedDocumentRepository of(@NonNull DocumentRepository documentRepository, @NonNull List<BaseSynchronizedGenerator<Document, Document.Id>> synchronizedDocumentGenerators, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        return new SynchronizedDocumentRepository(documentRepository, synchronizedDocumentGenerators, documentContentRepository, borrowingSessionRepository, reviewRepository);
    }

    protected SynchronizedDocumentRepository(@NonNull DocumentRepository documentRepository, @NonNull List<BaseSynchronizedGenerator<Document, Document.Id>> synchronizedDocumentGenerators, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        super(
            documentRepository, synchronizedDocumentGenerators,
            UniRelationRepositories.of(List.of(documentContentRepository)),
            BiRelationRepositories.of(List.of(borrowingSessionRepository, reviewRepository), List.of())
        );
    }
}
