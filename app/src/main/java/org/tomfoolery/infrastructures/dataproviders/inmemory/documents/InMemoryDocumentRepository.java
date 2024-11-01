package org.tomfoolery.infrastructures.dataproviders.inmemory.documents;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.inmemory.auth.abc.BaseInMemoryRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryDocumentRepository extends BaseInMemoryRepository<Document, Document.Id> implements DocumentRepository {}
