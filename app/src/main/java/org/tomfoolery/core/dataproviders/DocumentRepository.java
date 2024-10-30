package org.tomfoolery.core.dataproviders;

import org.tomfoolery.core.dataproviders.abc.SearchableRepository;
import org.tomfoolery.core.domain.Document;
import org.tomfoolery.core.utils.enums.DocumentAttributeName;

public interface DocumentRepository extends SearchableRepository<Document, Document.Id, DocumentAttributeName> {}
