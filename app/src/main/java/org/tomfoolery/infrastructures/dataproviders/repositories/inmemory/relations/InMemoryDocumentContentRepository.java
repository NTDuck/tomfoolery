package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryDocumentContentRepository extends BaseInMemoryRepository<DocumentContent, DocumentContent.Id> implements DocumentContentRepository {
}
