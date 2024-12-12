package org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.abc.BaseHybridRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.RetrievalDocumentRepository;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

public class HybridDocumentRepository extends BaseHybridRepository<Document, Document.Id> implements DocumentRepository {
    public static @NonNull HybridDocumentRepository of(@NonNull DocumentRepository persistenceDocumentRepository, @NonNull List<RetrievalDocumentRepository> retrievalDocumentRepositories) {
        return new HybridDocumentRepository(persistenceDocumentRepository, retrievalDocumentRepositories);
    }

    protected HybridDocumentRepository(@NonNull DocumentRepository persistenceDocumentRepository, @NonNull List<RetrievalDocumentRepository> retrievalDocumentRepositories) {
        super(persistenceDocumentRepository, retrievalDocumentRepositories);
    }
}
