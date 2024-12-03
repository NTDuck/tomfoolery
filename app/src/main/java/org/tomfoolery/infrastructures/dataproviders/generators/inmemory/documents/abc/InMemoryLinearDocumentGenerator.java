package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.Set;
import java.util.TreeSet;

public class InMemoryLinearDocumentGenerator implements BaseSynchronizedGenerator<Document, Document.Id> {
    protected @NonNull Set<Document> cachedDocuments = new TreeSet<>(DocumentComparator.byIdAscending);

    @Override
    public void synchronizeSavedEntity(@NonNull Document savedDocument) {
        this.cachedDocuments.add(savedDocument);
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Document deletedDocument) {
        this.cachedDocuments.remove(deletedDocument);
    }
}
