package org.tomfoolery.core.dataproviders.repositories.relations;

import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseUniRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;

public interface DocumentContentRepository extends BaseUniRepository<DocumentContent, DocumentContent.Id, Document.Id> {
    
}
