package org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.aggregates.BaseHybridRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

public class HybridDocumentRepository extends BaseHybridRepository<Document, Document.Id> implements DocumentRepository {
    public static @NonNull HybridDocumentRepository of(@NonNull List<DocumentRepository> persistenceDocumentRepositories, @NonNull List<DocumentRepository> retrievalDocumentRepositories) {
        return new HybridDocumentRepository(persistenceDocumentRepositories, retrievalDocumentRepositories);
    }

    protected HybridDocumentRepository(@NonNull List<DocumentRepository> persistenceDocumentRepositories, @NonNull List<DocumentRepository> retrievalDocumentRepositories) {
        super(persistenceDocumentRepositories, retrievalDocumentRepositories);
    }
}
