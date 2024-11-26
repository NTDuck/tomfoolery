package org.tomfoolery.infrastructures.dataproviders.repositories.hybrid.documents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.repositories.hybrid.abc.BaseHybridSynchronizableRepository;

import java.util.List;

public class HybridDocumentRepository extends BaseHybridSynchronizableRepository<Document, Document.Id> implements DocumentRepository {
    public static @NonNull HybridDocumentRepository of(@NonNull DocumentRepository documentRepository, @NonNull List<BaseRepository<Document, Document.Id>> retrievalRepositories) {
        return new HybridDocumentRepository(documentRepository, retrievalRepositories);
    }

    protected HybridDocumentRepository(@NonNull DocumentRepository documentRepository, @NonNull List<BaseRepository<Document, Document.Id>> retrievalRepositories) {
        super(documentRepository, retrievalRepositories);
    }
}
