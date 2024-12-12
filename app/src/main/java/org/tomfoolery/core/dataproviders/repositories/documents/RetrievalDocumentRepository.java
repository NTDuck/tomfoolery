package org.tomfoolery.core.dataproviders.repositories.documents;

import org.tomfoolery.core.dataproviders.repositories.abc.BaseRetrievalRepository;
import org.tomfoolery.core.domain.documents.Document;

public interface RetrievalDocumentRepository extends BaseRetrievalRepository<Document, Document.Id>, DocumentRepository {
}
