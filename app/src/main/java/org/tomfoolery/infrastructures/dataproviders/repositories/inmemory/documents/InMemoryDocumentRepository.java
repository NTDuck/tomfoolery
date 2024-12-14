package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.Comparator;

@NoArgsConstructor(staticName = "of")
public class InMemoryDocumentRepository extends BaseInMemoryRepository<Document, Document.Id> implements DocumentRepository {
    @Override
    protected @NonNull Comparator<Document.Id> getEntityIdComparator() {
        return DocumentComparator.compareId();
    }
}
