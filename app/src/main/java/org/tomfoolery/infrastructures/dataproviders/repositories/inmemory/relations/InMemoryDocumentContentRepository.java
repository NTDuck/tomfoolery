package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.Comparator;

@NoArgsConstructor(staticName = "of")
public class InMemoryDocumentContentRepository extends BaseInMemoryRepository<DocumentContent, DocumentContent.Id> implements DocumentContentRepository {
    @Override
    protected @NonNull Comparator<DocumentContent.Id> getEntityIdComparator() {
        return Comparator.comparing(DocumentContent.Id::getEntityId, DocumentComparator.compareId());
    }
}
