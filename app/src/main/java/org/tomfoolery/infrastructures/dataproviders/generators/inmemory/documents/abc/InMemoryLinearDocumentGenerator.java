package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.abc.BaseInMemorySynchronizedGenerator;

import java.util.HashSet;
import java.util.Set;

public class InMemoryLinearDocumentGenerator extends BaseInMemorySynchronizedGenerator<Document, Document.Id> {
    protected @NonNull Set<FragmentaryDocument> fragmentaryDocuments = new HashSet<>();

    @Override
    public void synchronizeRecentlySavedEntities(@NonNull Set<Document> savedEntities) {
        savedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(this.fragmentaryDocuments::add);
    }

    @Override
    public void synchronizeRecentlyDeletedEntities(@NonNull Set<Document> deletedEntities) {
        deletedEntities.parallelStream()
            .map(FragmentaryDocument::of)
            .forEach(this.fragmentaryDocuments::remove);
    }
}
