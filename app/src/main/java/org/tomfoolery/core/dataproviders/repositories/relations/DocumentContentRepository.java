package org.tomfoolery.core.dataproviders.repositories.relations;

import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseUniRelationRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;

public interface DocumentContentRepository extends BaseUniRelationRepository<DocumentContent, DocumentContent.Id, Document.Id> {
}
