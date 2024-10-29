package org.tomfoolery.core.dataproviders.documents;

import org.tomfoolery.core.dataproviders.abc.SearchableRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.enums.DocumentAttributeName;

public interface DocumentRepository extends SearchableRepository<Document, Document.Id, DocumentAttributeName> {}
